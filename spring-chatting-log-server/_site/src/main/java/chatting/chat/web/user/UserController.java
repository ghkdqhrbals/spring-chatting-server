package chatting.chat.web.user;


import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Participant;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.participant.service.ParticipantService;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.dto.*;
import chatting.chat.web.filters.cons.SessionConst;
import chatting.chat.web.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FriendService friendService;
    private final ChatService chatService;

    // 친구목록과 유저 정보 및 여러 기능을 표시하는 메인화면
    @GetMapping
    public String mainHome(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginUser, Model model){

        // 모델에 친구목록을 DTO에 담아 전달
        List<Friend> findFriends = friendService.findAll(loginUser);
        List<FriendInfoDTO> friendInfoDTOS = new ArrayList<>();
        for (Friend f : findFriends){
            User myFriend = userService.findByUserId(f.getFriendId());
            friendInfoDTOS.add(new FriendInfoDTO(myFriend.getUserId(),myFriend.getUserName(),myFriend.getUserStatus()));
        }
        model.addAttribute("findFriends",friendInfoDTOS);

        // 모델에 유저정보를 전달
        model.addAttribute("user",loginUser);

        return "users/loginHome";
    }

    // 유저 상태메세지 변경
    @GetMapping("/status")
    public String updateUserStatus(@ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form){
        return "users/updateStatusForm";
    }

    // 유저 상태메세지 변경
    @PostMapping("/status")
    public String updateUserStatusForm(HttpSession session, @ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form){
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 상태 메세지 변경
        userService.updateUserStatus(user.getUserId(),form.getStatusMessage());
        return "redirect:/";
    }

    // 유저 추가
    @GetMapping("/add")
    public String addUser(@ModelAttribute("userForm") UserForm form){
        return "users/addUserForm";
    }

    // 유저 추가
    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("userForm") UserForm form, BindingResult bindingResult){

        // Form 에러 모델 전달
        if (bindingResult.hasErrors()){
            return "users/addUserForm";
        }

        User findUser = userService.findByUserId(form.getUserId());

        // 기존 유저 확인
        if (findUser==null){
            bindingResult.rejectValue("userId","exist");
        }

        // 패스워드 설정 길이 확인
        if (4 > form.getUserPw().length() || form.getUserPw().length() > 10) {
            bindingResult.rejectValue("userPw","range",new Object[]{4,10},null);
        }

        // 이메일 형식 확인
        if (!form.getEmail().contains("@")){
            bindingResult.rejectValue("email","format");
        }

        if (bindingResult.hasErrors()){
            return "users/addUserForm";
        }

        // 유저저장
        User user = new User(form.getUserId(), form.getUserPw(), form.getEmail(), form.getUserName(), "",LocalDate.now(), LocalDate.now(), LocalDate.now());
        userService.save(user);

        return "redirect:/";
    }


    // 유저 존재유무 확인 RestAPI
    @GetMapping("/api/exist/{userId}")
    @ResponseBody
    public Optional<User> apiUserIdExist(@PathVariable("userId") String userId){
        Optional<User> user = userService.findById(userId);
        return user;
    }

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public String rooms(HttpSession session, Model model){

        // 유저가 참여하고있는 채팅방 목록 검색 후 모델 전달
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
        List<ChatRoomDTO> allMyRooms = userService.findAllMyRooms(user.getUserId());
        model.addAttribute("list", allMyRooms);

        return "users/rooms";
    }

    //채팅방 개설
    @GetMapping("/room")
    public String createRoom(@ModelAttribute("form") RoomCreationDTO roomsForm, HttpSession session, Model model){

        // 세션에 저장된 유저정보를 가져와 유저의 친구목록을 검색
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
        List<Friend> myFriends = userService.getMyFriends(user.getUserId());

        // DTO에 담아 모델에 전달
        for (Friend f : myFriends){
            roomsForm.addFriend(new CreateChatRoomDTO(f.getFriendId(), false));
        }

        return "users/room";
    }

    //채팅방 개설
    @PostMapping(value = "/room")
    public String createRoomForm(@ModelAttribute("form") RoomCreationDTO roomsForm, HttpSession session, Model model){
        List<CreateChatRoomDTO> friends = roomsForm.getFriends();
        List<String> friendsIdList = new ArrayList<>();

        // Form에서 전달받은 친구들의 ID List
        for (CreateChatRoomDTO f : friends){
            friendsIdList.add(f.getFriendId());
        }

        // 세션에서 유저정보를 검색
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 새로운 채팅방 생성
        userService.makeRoomWithFriends(user,friendsIdList);

        return "redirect:/users/rooms";
    }

    // 채팅방
    @GetMapping("/chat")
    public String chattingRoom(@RequestParam Long roomId, Model model, HttpSession session) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // Participant에서 Room참여자들 정보를 가져와 모델에 전달
        Participant findParticipant = userService.findByRoomIdAndUserId(roomId,user.getUserId());
        model.addAttribute("user", user);

        // 유저가 참여하고있는 채팅방정보를 DTO에 담아 모델에 전달
        RoomInfoDTO roomInfoDTO = new RoomInfoDTO();
        roomInfoDTO.setName(findParticipant.getRoomName());
        roomInfoDTO.setRoomId(roomId);
        model.addAttribute("room", roomInfoDTO);

        // 입장한 채팅방의 채팅메세지들을 가져와 모델에 전달
        List<Chatting> findChattings = chatService.findAllByRoomId(roomId);
        model.addAttribute("records",findChattings);

        return "/users/chat";
    }





}

package chatting.chat.web.user;


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

    // 여러개의 service를 사용하니까 트랜젝션 관리 조심
    // -> 하나의 Handler에서 한번의 수정만 가능하도록!
    private final UserService userService;
    private final FriendService friendService;
    
    @GetMapping
    public String mainHome(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginUser, Model model){
        List<Friend> findFriends = friendService.findAll(loginUser);

        List<FriendInfoDTO> friendInfoDTOS = new ArrayList<>();

        // 변환과정을 가지는게 맞을까???
        for (Friend f : findFriends){
            Optional<User> myFriend = userService.findById(f.getFriendId());
            friendInfoDTOS.add(new FriendInfoDTO(myFriend.get().getUserId(),myFriend.get().getUserName(),myFriend.get().getUserStatus()));
        }

        model.addAttribute("findFriends",friendInfoDTOS);
        model.addAttribute("user",loginUser);
        return "users/loginHome";
    }

    @GetMapping("/status")
    public String updateUserStatus(@ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form){
        return "users/updateStatusForm";
    }

    @PostMapping("/status")
    public String updateUserStatusForm(HttpSession session, @ModelAttribute("userStatusUpdateDTO") UserStatusUpdateDTO form){
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
        userService.updateUserStatus(user.getUserId(),form.getStatusMessage());
        return "redirect:/";
    }

    @GetMapping("/add")
    public String addUser(@ModelAttribute("userForm") UserForm form){
        return "users/addUserForm";
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("userForm") UserForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "users/addUserForm";
        }

        Optional<User> findUser = userService.findById(form.getUserId());

        // 기존 유저 확인
        if (findUser.isPresent()){
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

        User user = new User(form.getUserId(), form.getUserPw(), form.getEmail(), form.getUserName(), "",LocalDate.now(), LocalDate.now(), LocalDate.now());

        userService.save(user);

        return "redirect:/";
    }


    @GetMapping("/api/exist/{userId}")
    @ResponseBody
    public Optional<User> apiUserIdExist(@PathVariable("userId") String userId){
        Optional<User> user = userService.findById(userId); // 존재하는 사용자인지 확인
        return user;
    }

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public String rooms(HttpSession session, Model model){
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);


        List<ChatRoomDTO> allMyRooms = userService.findAllMyRooms(user.getUserId());


        model.addAttribute("list", allMyRooms);
        return "users/rooms";
    }

    /**
     * 1. 친구 List 중, 친구들 선택(Search)
     * 2. 선택된 친구들을 participantService 저장(Modified)
     */

    //채팅방 개설
    @GetMapping("/room")
    public String createRoom(@ModelAttribute("form") RoomCreationDTO roomsForm, HttpSession session, Model model){

        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
        List<Friend> myFriends = userService.getMyFriends(user.getUserId());
        log.info("myFriend count : {}",myFriends.size());

        for (Friend f : myFriends){
            log.info("친구 ID : {}",f.getFriendId());
            roomsForm.addFriend(new CreateChatRoomDTO(f.getFriendId(), false));
        }

        return "users/room";
    }

    /**
     * 1. 웹소켓(stomp) 연결(Modified)
     * 2. participantService에 roomId일치하는 유저 list 탐색(Search)
     *
     */
    //채팅방 개설
    @PostMapping(value = "/room")
    public String createRoomForm(@ModelAttribute("form") RoomCreationDTO roomsForm, HttpSession session, Model model){
        List<CreateChatRoomDTO> friends = roomsForm.getFriends();
        List<String> friendsIdList = new ArrayList<>();

        for (CreateChatRoomDTO f : friends){
            friendsIdList.add(f.getFriendId());
        }

        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
        userService.makeRoomWithFriends(user,friendsIdList);

        return "redirect:/users/rooms";
    }

    @GetMapping("/chat")
    public String chattingRoom(@RequestParam Long roomId, Model model, HttpSession session) {
        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // Participant에서 room 참여자들 정보를 가져와야됨
        Participant findParticipant = userService.findByRoomIdAndUserId(roomId,user.getUserId());

        RoomInfoDTO roomInfoDTO = new RoomInfoDTO();
        roomInfoDTO.setName(findParticipant.getRoomName());
        roomInfoDTO.setRoomId(roomId);

        model.addAttribute("room", roomInfoDTO);
        model.addAttribute("user", user);
        return "/users/chat";
    }





}

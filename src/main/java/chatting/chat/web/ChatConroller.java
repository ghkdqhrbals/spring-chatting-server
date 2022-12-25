//package chatting.chat.web;
//
//import chatting.chat.domain.chat.ChatService;
//import chatting.chat.domain.data.*;
//import chatting.chat.domain.friend.service.FriendService;
//import chatting.chat.domain.room.service.RoomService;
//import chatting.chat.domain.user.service.UserService;
//import chatting.chat.web.kafka.dto.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpSession;
//import javax.validation.Valid;
//import java.time.LocalDate;
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Controller
//@RequestMapping("/user")
//@RequiredArgsConstructor
//public class ChatConroller {
//
//    private final UserService userService;
//    private final FriendService friendService;
//    private final RoomService roomService;
//    private final ChatService chatService;
//
//    // 유저 저장
//    @PostMapping("/")
//    @ResponseBody
//    public ResponseAddUserDTO addUser(@RequestBody RequestAddUserDTO req){
//        User user = new User(
//                req.getUserId(),
//                req.getUserPw(),
//                req.getEmail(),
//                req.getUserName(),
//                "",
//                LocalDate.now(),
//                LocalDate.now(),
//                LocalDate.now()
//        );
//        return userService.save(user);
//    }
//
//    // 유저 상태메세지 변경
//    @PostMapping("/status")
//    @ResponseBody
//    public ResponseChangeUserStatusDTO updateUserStatus(@RequestBody RequestChangeUserStatusDTO req){
//        return userService.updateUserStatus(req);
//    }
//
//    //채팅방 목록 조회
//
//    //채팅방 개설
//    @PostMapping("/room")
//    public ResponseEntity<?> createRoom(@RequestBody RequestAddUserRoomDTO req){
//        ResponseAddUserRoomDTO resp = userService.makeRoomWithFriends(req);
//
//        return userService.makeRoomWithFriends(req);
//    }
//
//    // 이전 채팅 가져오기
//    @GetMapping("/room/{roomId}")
//    public ResponseEntity<?> chattingRoom(@RequestParam Long roomId) {
//        List<Chatting> chattings = chatService.findAllByRoomId(roomId);
//        if (chattings.size()==0){
//            return ResponseEntity.badRequest().body("빈 채팅목록");
//        }
//
//        User user = (User) session.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        // Participant에서 Room참여자들 정보를 가져와 모델에 전달
//        Participant findParticipant = userService.findByRoomIdAndUserId(roomId,user.getUserId());
//        model.addAttribute("user", user);
//
//        // 유저가 참여하고있는 채팅방정보를 DTO에 담아 모델에 전달
//        RoomInfoDTO roomInfoDTO = new RoomInfoDTO();
//        roomInfoDTO.setName(findParticipant.getRoomName());
//        roomInfoDTO.setRoomId(roomId);
//        model.addAttribute("room", roomInfoDTO);
//
//        // 입장한 채팅방의 채팅메세지들을 가져와 모델에 전달
//        List<Chatting> findChattings = chatService.findAllByRoomId(roomId);
//        model.addAttribute("records",findChattings);
//
//        return "/users/chat";
//    }
//
//    // 채팅 저장
//    @PostMapping("/chat")
//    public ResponseAddChatMessageDTO addChatMeesage(@RequestBody RequestAddChatMessageDTO req){
//        Optional<Room> room = roomService.findByRoomId(req.getRoomId());
//        Optional<User> user = userService.findById(req.getWriterId());
//
//        ResponseAddChatMessageDTO resp = new ResponseAddChatMessageDTO(req.getRoomId(),req.getWriterId());
//        if (!room.isPresent()){
//            resp.setErrorMessage("채팅방이 존재하지 않습니다");
//        }
//        if(!user.isPresent()){
//            resp.setErrorMessage("유저가 존재하지 않습니다");
//        }
//
//        Chatting chat = createChatting(user.get(),room.get(),req.getMessage());
//
//        return chatService.save(chat);
//    }
//
//    // 친구 저장
//    @PostMapping("/friend")
//    public ResponseAddFriendDTO addFriend(@RequestBody RequestAddFriendDTO req){
//        List<String> friendIds = req.getFriendId();
//        ResponseAddFriendDTO resp = new ResponseAddFriendDTO(req.getUserId());
//
//        for(String friendId : friendIds){
//            Optional<User> findId = userService.findById(friendId);
//            if (!findId.isPresent()){
//                resp.setErrorMessage("유저가 존재하지 않습니다");
//                break;
//            }
//            friendService.save(req.getUserId(), friendId);
//        }
//
//        return resp;
//    }
//
//    // utils
//    private Chatting createChatting(User user,Room room, String message){
//        Chatting chat = new Chatting();
//        chat.setRoom(room);
//        chat.setSendUser(user);
//        chat.setCreatedDate(ZonedDateTime.now().toLocalDate());
//        chat.setCreatedTime(ZonedDateTime.now().toLocalTime());
//        chat.setMessage(message);
//        return chat;
//    }
//}

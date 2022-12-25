package chatting.chat.web;

import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.data.*;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.kafka.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FriendService friendService;
    private final RoomService roomService;
    private final ChatService chatService;

    /**
     * -------------- GET METHODS --------------
     */
    // 유저 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findUser(@PathVariable("userId") String userId){
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    // 채팅방 정보 조회
    @GetMapping(value = "/room/{roomId}")
    public ResponseEntity<?> findRoom(@PathVariable("roomId") Long roomId){
        Room room = roomService.findByRoomId(roomId);
        return ResponseEntity.ok(room);
    }

    // 유저가 참여하고 있는 채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public ResponseEntity<?> findRoomWithUserId(@RequestParam("userId") String userId){
        List<ChatRoomDTO> findUserRooms = userService.findAllMyRooms(userId);
        return ResponseEntity.ok(findUserRooms);
    }

    // 유저의 친구목록 조회
    @GetMapping("/friend")
    public ResponseEntity<?> findFriend(@RequestParam("userId") String userId){
        User findUser = userService.findById(userId);
        List<Friend> findFriends = friendService.findAllByUserId(findUser.getUserId());
        return ResponseEntity.ok(findFriends);
    }

    // 이전 채팅기록 조회
    @GetMapping(value = "/chat/room")
    public ResponseEntity<?> findChatRecords(@RequestParam("roomId") Long roomId){
        Room findRoom = roomService.findByRoomId(roomId);
        List<Chatting> findChattings = chatService.findAllByRoomId(findRoom.getRoomId());
        return ResponseEntity.ok(findChattings);
    }

    // 채팅 조회
    @GetMapping(value = "/chat")
    public ResponseEntity<?> findChatRecord(@RequestParam("chatId") Long chatId){
        Chatting findChatting = chatService.findById(chatId);
        return ResponseEntity.ok(findChatting);
    }

    /**
     * -------------- POST METHODS --------------
     */
    // 유저 저장
    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody RequestAddUserDTO request){
        User user = new User(
                request.getUserId(),
                request.getUserPw(),
                request.getEmail(),
                request.getUserName(),
                "",
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        );
        User save = userService.save(user);
        return ResponseEntity.ok(save);
    }


    // 유저 상태메세지 변경
    @PostMapping("/status")
    public ResponseEntity<?> changeUserStatus(@RequestBody RequestChangeUserStatusDTO request){
        userService.updateUserStatus(request);
        return ResponseEntity.ok("success");
    }

    // 채팅방 개설
    @PostMapping(value = "/room")
    public ResponseEntity<?> addChatRoom(@RequestBody RequestAddChatRoomDTO req){
        userService.makeRoomWithFriends(req);
        return ResponseEntity.ok("success");
    }

    // 채팅 저장
    @PostMapping("/chat")
    public ResponseEntity<?> addChat(@RequestBody RequestAddChatMessageDTO req) {

        // validation
        Room findRoom = roomService.findByRoomId(req.getRoomId());
        User findUser = userService.findById(req.getWriterId());

        userService.findByRoomIdAndUserId(findRoom.getRoomId(), findUser.getUserId());

        // logic
        Chatting chatting = convertToChatting(findRoom, findUser, req.getMessage());
        Chatting save = chatService.save(chatting);

        return ResponseEntity.ok(save);
    }

    // 친구 저장
    @PostMapping("/friend")
    public ResponseEntity<?> addFriend(@RequestBody RequestAddFriendDTO req){

        // validation
        User findUser = userService.findById(req.getUserId());
        List<Friend> friends = new ArrayList<>();

        List<String> friendIds = req.getFriendId();
        for(String friendId : friendIds){

            User findUserFriend = userService.findById(friendId);

            // logic
            Friend save = friendService.save(findUser.getUserId(), findUserFriend.getUserId());
            friends.add(save);
        }
        return ResponseEntity.ok(friends);
    }


    // utils
    private Chatting convertToChatting(Room room, User user,String msg){
        Chatting chatting = new Chatting();
        chatting.setRoom(room);
        chatting.setSendUser(user);
        chatting.setCreatedDate(LocalDateTime.now().toLocalDate());
        chatting.setCreatedTime(LocalDateTime.now().toLocalTime());
        chatting.setMessage(msg);
        return chatting;
    }

}

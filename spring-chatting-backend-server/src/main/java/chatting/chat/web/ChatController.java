package chatting.chat.web;

import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.data.*;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.participant.service.ParticipantService;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.dto.RequestUser;
import chatting.chat.web.dto.ResponseGetFriend;
import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.kafka.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@RestController
public class ChatController {
    private final UserService userService;
    private final FriendService friendService;
    private final RoomService roomService;
    private final ChatService chatService;

    private final ParticipantService participantService;

    public ChatController(UserService userService, FriendService friendService, RoomService roomService, ChatService chatService,ParticipantService participantService) {
        this.userService = userService;
        this.friendService = friendService;
        this.roomService = roomService;
        this.chatService = chatService;
        this.participantService = participantService;
    }
    /**
     * -------------- GET METHODS --------------
     */

    // 유저 조회
    @GetMapping("/user")
    public ResponseEntity<?> findUser(@RequestParam("userId") String userId){
        User findUser = userService.findById(userId);
        return ResponseEntity.ok(new ResponseGetUser(findUser.getUserId(),findUser.getUserName(),findUser.getUserStatus()));
    }

    // 유저 추가
    @PostMapping("/user")
    public ResponseEntity<?> addUser(RequestUser req){
        return ResponseEntity.ok(userService.save(req.getUserId(),req.getUserName(),""));
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

        Stream<ResponseGetFriend> rGetFriend = findUser.getFriends().stream().map(f -> {
            User findFriend = userService.findById(f.getFriendId());
            return new ResponseGetFriend(findFriend.getUserId(), findFriend.getUserName(),findFriend.getUserStatus());
        });
        List<ResponseGetFriend> collect = rGetFriend.collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

    // 이전 채팅기록 조회
    @GetMapping("/chats")
    public ResponseEntity<?> findChatRecords(@RequestParam("roomId") Long roomId){
        Room findRoom = roomService.findByRoomId(roomId);
        List<Chatting> findChattings = chatService.findAllByRoomId(findRoom.getRoomId());
        List<ChatRecord> response = findChattings.stream().map(c -> new ChatRecord(c.getId(), c.getRoom().getRoomId(), c.getSendUser().getUserId(), c.getSendUser().getUserName(), c.getMessage(), c.getCreatedAt())).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 특정 채팅 조회
    @GetMapping(value = "/chat")
    public ResponseEntity<?> findChatRecord(@RequestParam("chatId") Long chatId){
        Chatting findChatting = chatService.findById(chatId);
        return ResponseEntity.ok(findChatting);
    }

    /**
     * -------------- POST METHODS --------------
     */

    // 유저 상태메세지 변경
    @PostMapping("/status")
    public ResponseEntity<?> changeUserStatus(@RequestBody RequestChangeUserStatusDTO request){
        userService.updateUserStatus(request);
        return ResponseEntity.ok("success");
    }

    // 채팅방 개설
    @PostMapping("/room")
    public ResponseEntity<?> addChatRoom(@RequestBody RequestAddChatRoomDTO req){
        userService.makeRoomWithFriends(req);
        List<ChatRoomDTO> allMyRooms = userService.findAllMyRooms(req.getUserId());
        return ResponseEntity.ok(allMyRooms);
    }

    // 채팅 저장
    @PostMapping(value = "/chat")
    public ResponseEntity<?> addChat(@RequestBody RequestAddChatMessageDTO req) {

        // validation
        Room findRoom = roomService.findByRoomId(req.getRoomId());
        User findUser = userService.findById(req.getWriterId());

        // service-logic
        Chatting chatting = createChatting(findRoom, findUser, req.getMessage());
        chatService.save(chatting);

        return ResponseEntity.ok("success");
    }

    // 친구 저장
    @PostMapping("/friend")
    public ResponseEntity<?> addFriend(@RequestBody RequestAddFriendDTO req){

        // validation
        User findUser = userService.findById(req.getUserId());

        List<String> friendIds = req.getFriendId();
        for(String friendId : friendIds){

            User findUserFriend = userService.findById(friendId);

            // logic
            friendService.save(findUser.getUserId(), findUserFriend.getUserId());
        }
        return ResponseEntity.ok("success");
    }

    /**
     * DELETE
     */

    @DeleteMapping("/friend")
    public ResponseEntity<?> removeFriend(@RequestParam("userId") String userId,@RequestParam("friendId") String friendId){
        friendService.removeFriend(userId,friendId);
        return ResponseEntity.ok("success");
    }


    // utils
    private Chatting createChatting(Room room, User user, String msg){
        Chatting chatting = new Chatting();
        chatting.setId(UUID.randomUUID().toString());
        chatting.setRoom(room);
        chatting.setSendUser(user);
        chatting.setCreatedAt(LocalDateTime.now());
        chatting.setMessage(msg);
        return chatting;
    }

}

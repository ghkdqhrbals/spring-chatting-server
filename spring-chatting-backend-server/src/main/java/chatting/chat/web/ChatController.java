package chatting.chat.web;

import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.data.*;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.kafka.KafkaTopicConst;
import chatting.chat.web.kafka.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController extends KafkaTopicConst {
    private final UserService userService;
    private final FriendService friendService;
    private final RoomService roomService;
    private final ChatService chatService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
    public ChatController(UserService userService, FriendService friendService, RoomService roomService, ChatService chatService, KafkaTemplate<String, Object> kafkaProducerTemplate) {
        this.userService = userService;
        this.friendService = friendService;
        this.roomService = roomService;
        this.chatService = chatService;
        this.kafkaProducerTemplate = kafkaProducerTemplate;
    }
    /**
     * -------------- GET METHODS --------------
     */
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
    @GetMapping("/chats")
    public ResponseEntity<?> findChatRecords(@RequestParam("roomId") Long roomId){
        Room findRoom = roomService.findByRoomId(roomId);
        List<Chatting> findChattings = chatService.findAllByRoomId(findRoom.getRoomId());
        return ResponseEntity.ok(findChattings);
    }

    // 특정 채팅 조회
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<?> findChatRecord(@RequestParam("chatId") Long chatId){
        Chatting findChatting = chatService.findById(chatId);
        return ResponseEntity.ok(findChatting);
    }

    /**
     * -------------- POST METHODS --------------
     */
    // 유저 저장
    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody RequestAddUserDTO req){

        // service-logic
        User save = userService.save(req.getUserId(),req.getUserName(),"");

        // kafka-logic
        sendToKafka(TOPIC_USER_ADD_REQUEST,req);

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
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<?> addChat(@RequestBody RequestAddChatMessageDTO req) {

        // validation
        Room findRoom = roomService.findByRoomId(req.getRoomId());
        User findUser = userService.findById(req.getWriterId());

        userService.findByRoomIdAndUserId(findRoom.getRoomId(), findUser.getUserId());

        // service-logic
        Chatting chatting = convertToChatting(findRoom, findUser, req.getMessage());
        Chatting save = chatService.save(chatting);

        // kafka-logic
        sendToKafka(TOPIC_USER_ADD_CHAT_REQUEST,req);

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
        return ResponseEntity.ok("성공");
    }

    /**
     * DELETE
     */
    @DeleteMapping("/user")
    public ResponseEntity<?> removeUser(@RequestParam("userId") String userId){
        log.info("REQUEST DELETE");
        userService.removeUser(userId);
        return ResponseEntity.ok("성공");
    }

    @DeleteMapping("/friend")
    public ResponseEntity<?> removeFriend(@RequestParam("userId") String userId,@RequestParam("friendId") String friendId){
        log.info("REQUEST DELETE");
        friendService.removeFriend(userId,friendId);
        return ResponseEntity.ok("성공");
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

    private void sendToKafka(String topic,Object req) {
        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(topic, req);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("메세지 전송 실패={}", ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("메세지 전송 성공 topic={}, offset={}, partition={}",topic, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }
        });
    }

}

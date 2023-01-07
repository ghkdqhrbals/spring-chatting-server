package chatting.chat.web.kafka;


import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.kafka.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MessageListener extends KafkaTopicConst{
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    public MessageListener(KafkaTemplate<String, Object> kafkaProducerTemplate) {
        this.kafkaProducerTemplate = kafkaProducerTemplate;
    }

    // 로그인 요청
    @KafkaListener(topics = "${kafka.topic-login-response}", containerFactory = "loginKafkaListenerContainerFactory")
    public void listenLogin(RequestLoginDTO req) {
        log.info("Receive [RequestLoginDTO] Message with userId={},userPw={}",req.getUserId(),req.getUserPw());



//        ResponseLoginDTO resp = userService.login(req.getUserId(), req.getUserPw());

    }
//
//    // 로그아웃 요청
//    @KafkaListener(topics = "${kafka.topic-logout-response}", containerFactory = "logoutKafkaListenerContainerFactory")
//    public void listenLogin(@Payload String userId) {
//        ResponseLogoutDTO resp = userService.logout(userId);
//
//        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_LOGOUT_RESPONSE, userId, resp);
//        listenFuture(future);
//    }
//
//    // 유저 참여 채팅방 목록 조회
//    @KafkaListener(topics = "${kafka.topic-user-search-room-response}", containerFactory = "userRoomKafkaListenerContainerFactory")
//    public void rooms(RequestUserRoomDTO req){
//        ResponseUserRoomDTO resp = userService.findAllMyRooms(req.getUserId());
//
//        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_USER_SEARCH_ROOM_RESPONSE, req.getUserId(),resp);
//        listenFuture(future);
//    }
//
//    // 유저의 친구목록 조회
//    @KafkaListener(topics = "${kafka.topic-user-search-friend-response}", containerFactory = "userSearchFriendKafkaListenerContainerFactory")
//    public void findFriend(RequestUserFriendDTO req){
//        String userId = req.getUserId();
//
//        ResponseUserFriendDTO resp = new ResponseUserFriendDTO(userId);
//
//        Optional<User> findUser = userService.findById(userId);
//
//        if (!findUser.isPresent()){
//            resp.setStat(userId + "유저가 존재하지 않습니다");
//        }else{
//            List<Friend> friends = friendService.findAllByUserId(userId);
//            resp.setIsSuccess(true);
//            resp.setFriend(friends);
//        }
//        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_USER_SEARCH_FRIEND_RESPONSE, req.getUserId(),resp);
//        listenFuture(future);
//    }
//
//    // 유저 저장
//    @KafkaListener(topics = "${kafka.topic-user-add-response}", containerFactory = "userAddKafkaListenerContainerFactory")
//    public void addUser(RequestAddUserDTO req){
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
//
//        ResponseAddUserDTO resp = userService.save(user);
//
//        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_USER_ADD_RESPONSE, req.getUserId(),resp);
//        listenFuture(future);
//    }
//
//    // 유저 상태메세지 변경
//    @KafkaListener(topics = "${kafka.topic-user-status-change-response}", containerFactory = "userChangeStatusKafkaListenerContainerFactory")
//    public void changeUserStatus(RequestChangeUserStatusDTO req){
//        ResponseChangeUserStatusDTO resp = userService.updateUserStatus(req);
//
//        ListenableFuture<SendResult<String, Object>> future =
//                kafkaProducerTemplate.send(TOPIC_USER_STATUS_CHANGE_RESPONSE, req.getUserId(),resp);
//        listenFuture(future);
//    }
//
//    //채팅방 개설
//    @KafkaListener(topics = "${kafka.topic-user-add-room-response}", containerFactory = "userAddRoomKafkaListenerContainerFactory")
//    public void createRoomForm(RequestAddUserRoomDTO req){
//        ResponseAddUserRoomDTO resp = userService.makeRoomWithFriends(req);
//
//        ListenableFuture<SendResult<String, Object>> future =
//                kafkaProducerTemplate.send(TOPIC_USER_ADD_ROOM_RESPONSE, req.getUserId(),resp);
//        listenFuture(future);
//    }
//
//    // 채팅 저장
//    @KafkaListener(topics = "${kafka.topic-user-add-chat-response}", containerFactory = "userAddChatKafkaListenerContainerFactory")
//    public void chattingRoom(RequestAddChatMessageDTO req) {
//
//        Optional<Room> room = roomService.findByRoomId(req.getRoomId());
//        Optional<User> user = userService.findById(req.getWriterId());
//
//        ResponseAddChatMessageDTO resp = new ResponseAddChatMessageDTO(req.getRoomId(),req.getWriterId());
//
//        if (!room.isPresent()){
//            resp.setErrorMessage("채팅방이 존재하지 않습니다");
//        }
//        if(!user.isPresent()){
//            resp.setErrorMessage("유저가 존재하지 않습니다");
//        }
//
//        Chatting chat = createChatting(user.get(),room.get(),req.getMessage());
//
//        resp = chatService.save(chat);
//
//        ListenableFuture<SendResult<String, Object>> future =
//                kafkaProducerTemplate.send(TOPIC_USER_ADD_CHAT_RESPONSE, req.getWriterId(),resp);
//        listenFuture(future);
//    }
//
//    // 친구 저장
//    @KafkaListener(topics = "${kafka.topic-user-add-friend-request}", containerFactory = "userAddFriendKafkaListenerContainerFactory")
//    public void addFriend(RequestAddFriendDTO req){
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
//        ListenableFuture<SendResult<String, Object>> future =
//                kafkaProducerTemplate.send(TOPIC_USER_ADD_FRIEND_RESPONSE, req.getUserId(),resp);
//        listenFuture(future);
//    }


    // utils
    private Chatting createChatting(User user,Room room, String message){
        Chatting chat = new Chatting();
        chat.setRoom(room);
        chat.setSendUser(user);
        chat.setCreatedDate(ZonedDateTime.now().toLocalDate());
        chat.setCreatedTime(ZonedDateTime.now().toLocalTime());
        chat.setMessage(message);
        return chat;
    }

    private void listenFuture(ListenableFuture<SendResult<String, Object>> future) {
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("메세지 전송 실패 : {}", ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("메세지 전송 성공 topic: {}, offset: {}, partition: {}",result.getRecordMetadata().topic() ,result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }
        });
    }
}
package chatting.chat.domain.user.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Participant;
import chatting.chat.domain.data.User;
import chatting.chat.web.kafka.dto.*;

import java.util.List;
import java.util.Optional;


public interface UserService {
    User save(String userId, String userName, String userStatus);
    void updateUserStatus(RequestChangeUserStatusDTO req);
    List<ChatRoomDTO> findAllMyRooms(String userId);
    void makeRoomWithFriends(RequestAddChatRoomDTO req);
    User findById(String id);
    void removeUser(String userId);
    List<Participant> findParticipantWithRoomId(Long roomId);
    List<Friend> getMyFriends(String userId);
    Participant findByRoomIdAndUserId(Long roomId, String userId);


}

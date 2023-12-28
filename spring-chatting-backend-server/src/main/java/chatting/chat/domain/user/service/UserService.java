package chatting.chat.domain.user.service;

import chatting.chat.domain.friend.entity.Friend;
import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.room.dto.RoomDto;
import chatting.chat.domain.user.entity.User;
import com.example.commondto.dto.friend.FriendResponse;
import chatting.chat.web.kafka.dto.*;

import java.util.List;


public interface UserService {
    List<FriendResponse.FriendDTO> findAllFriends(String userId);
    User save(String userId, String userName, String userStatus);
    void updateUserStatus(RequestChangeUserStatusDTO req);
    List<ChatRoomDTO> findAllMyRooms(String userId);
    RoomDto makeRoomWithFriends(RequestAddChatRoomDTO req);
    User findById(String id);
    void removeUser(String userId);
    List<Participant> findParticipantWithRoomId(Long roomId);
    List<Friend> getMyFriends(String userId);
    Participant findByRoomIdAndUserId(Long roomId, String userId);
    void remove(String userId);


}

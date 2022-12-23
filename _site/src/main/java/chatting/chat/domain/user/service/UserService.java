package chatting.chat.domain.user.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Participant;
import chatting.chat.domain.data.User;
import chatting.chat.web.dto.ChatRoomDTO;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;


public interface UserService {
    User save(User item);

    void updateUserStatus(String userId, String userStatus);

    void makeRoomWithFriends(User user, List<String> friendId);

    Optional<User> findById(String id);

    @Nullable
    User findByUserId(String id);

    List<ChatRoomDTO> findAllMyRooms(String userId);
    List<Participant> findParticipantWithRoomId(Long roomId);

    List<Friend> getMyFriends(String userId);

    Participant findByRoomIdAndUserId(Long roomId, String userId);


}

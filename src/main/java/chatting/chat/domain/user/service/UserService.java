package chatting.chat.domain.user.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Participant;
import chatting.chat.domain.data.User;
import chatting.chat.web.kafka.dto.*;

import java.util.List;
import java.util.Optional;


public interface UserService {
    ResponseAddUserDTO save(User item);
    ResponseChangeUserStatusDTO updateUserStatus(RequestChangeUserStatusDTO req);
    ResponseUserRoomDTO findAllMyRooms(String userId);

    ResponseAddUserRoomDTO makeRoomWithFriends(RequestAddUserRoomDTO req);

    Optional<User> findById(String id);

    ResponseLoginDTO login(String userId, String userPw);
    ResponseLogoutDTO logout(String userId);

    List<Participant> findParticipantWithRoomId(Long roomId);

    List<Friend> getMyFriends(String userId);

    Participant findByRoomIdAndUserId(Long roomId, String userId);


}

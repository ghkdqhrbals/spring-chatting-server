package chatting.chat.domain.friend.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;

import java.util.List;
import java.util.Optional;

public interface FriendService {

    String save(String userId, String friendId);

    Optional<Friend> findByUserAndFriend(User user, Friend friend);

    List<Friend> findAll(User user);

}

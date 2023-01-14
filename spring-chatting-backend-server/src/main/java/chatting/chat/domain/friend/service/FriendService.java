package chatting.chat.domain.friend.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;

import java.util.List;
import java.util.Optional;

public interface FriendService {

    Friend save(String userId, String friendId);
    void removeFriend(String userId, String friendId);

    List<Friend> findAllByUserId(String userId);


}

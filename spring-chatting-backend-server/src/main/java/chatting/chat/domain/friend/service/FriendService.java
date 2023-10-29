package chatting.chat.domain.friend.service;

import chatting.chat.domain.friend.entity.Friend;

import java.util.List;

public interface FriendService {

    Friend save(String userId, String friendId);
    void removeFriend(String userId, String friendId);

    List<Friend> findAllByUserId(String userId);


}

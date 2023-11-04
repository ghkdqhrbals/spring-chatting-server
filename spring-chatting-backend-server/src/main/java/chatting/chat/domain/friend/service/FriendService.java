package chatting.chat.domain.friend.service;

import chatting.chat.domain.friend.entity.Friend;

import com.example.commondto.dto.friend.FriendResponse;
import java.util.List;

public interface FriendService {

    Friend save(String userId, String friendId);
    void removeFriend(String userId, String friendId);

    FriendResponse.FriendDTO findMyFriend(String userId, String friendId);

    List<Friend> findAllByUserId(String userId);


}

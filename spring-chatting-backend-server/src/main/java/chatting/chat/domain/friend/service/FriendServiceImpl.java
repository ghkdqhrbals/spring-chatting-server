package chatting.chat.domain.friend.service;

import static com.example.commondto.error.ErrorCode.*;

import com.example.commondto.dto.friend.FriendResponse.FriendDTO;
import com.example.commondto.error.CustomException;
import chatting.chat.domain.friend.entity.Friend;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.user.repository.UserRepository;

import com.example.commondto.dto.friend.FriendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendServiceImpl(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "friendsList", key = "#userId")
    public FriendResponse.FriendDTO save(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new CustomException(CANNOT_ADD_SELF);
        }

        User findUser = getUserById(userId);
        User findFriend = getUserById(friendId);

        if (areFriends(userId, friendId)) {
            throw new CustomException(ALREADY_FRIEND);
        }

        Friend myFriend = new Friend(findUser, findFriend.getUserId());
        Friend hisFriend = new Friend(findFriend, findUser.getUserId());

        Friend savedFriend = friendRepository.save(myFriend);

        if (!areFriends(friendId, userId)) {
            friendRepository.save(hisFriend);
        }

        FriendResponse.FriendDTO friendDTO = FriendDTO.builder()
            .friendId(findFriend.getUserId())
            .friendName(findFriend.getUserName())
            .friendStatus(findFriend.getUserStatus())
            .build();
        return friendDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public FriendResponse.FriendDTO findMyFriend(String userId, String friendId) {
        Friend findFriends = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (findFriends == null) {
            throw new CustomException(CANNOT_FIND_USER);
        }
        User user = getUserById(friendId);

        return FriendResponse.FriendDTO.builder()
            .friendId(user.getUserId())
            .friendName(user.getUserName())
            .friendStatus(user.getUserStatus())
            .build();
    }

    @Override
    @Transactional
    public void removeFriend(String userId, String friendId) {
        Friend findFriend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (findFriend != null) {
            friendRepository.delete(findFriend);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friend> findAllByUserId(String userId) {
        return friendRepository.findAllByUserId(userId);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));
    }

    @Override
    public boolean areFriends(String userId, String friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId) != null;
    }
}

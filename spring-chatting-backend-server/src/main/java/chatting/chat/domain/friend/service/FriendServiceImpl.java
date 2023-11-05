package chatting.chat.domain.friend.service;

import static com.example.commondto.error.ErrorCode.*;
import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
import com.example.commondto.error.AppException;

import chatting.chat.domain.friend.entity.Friend;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.user.repository.UserRepository;

import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.dto.friend.FriendResponse.FriendDTO;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;



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
    public Friend save(String userId, String friendId) {
        // validation
        log.trace("user id : {}", userId);
        Optional<User> findUser = userRepository.findById(userId);
        Optional<User> findUserFriend = userRepository.findById(friendId);

        if (findUser.isEmpty() || findUserFriend.isEmpty()) {
            log.trace("empty user");
            throw new CustomException(CANNOT_FIND_USER);
        }
        log.trace("user exist!");

        Friend isFriend1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
        Friend isFriend2 = friendRepository.findByUserIdAndFriendId(friendId, userId);

        if (userId.equals(friendId)) {
            log.trace("cannot add self!");
            throw new CustomException(CANNOT_ADD_SELF);
        }
        log.trace("friend with me? : {}", isFriend1 != null ? "true" : "false");
        log.trace("me with friend? : {}", isFriend2 != null ? "true" : "false");

        // 이미 나와 친구임
        if (isFriend1 != null) {
            log.trace("already friend!");
            throw new CustomException(ALREADY_FRIEND);
        }
        log.trace("not friend!");

        // logic
        Friend friend1 = new Friend(findUser.get(), findUserFriend.get().getUserId());
        Friend friend2 = new Friend(findUserFriend.get(), findUser.get().getUserId());
        log.trace("friend create!");

        Friend save = friendRepository.save(friend1);
        log.trace("now friend!");

        if (isFriend2 == null) {
            friendRepository.save(friend2); // 상대방또한 나를 친구추가
            log.trace("friend add me!");
        }
        return save;
    }

    @Override
    @Transactional(readOnly = true)
    public FriendResponse.FriendDTO findMyFriend(String userId, String friendId) {
        Friend findFriends = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (findFriends==null){
            new CustomException(CANNOT_FIND_USER);
        }
        User user = userRepository.findById(friendId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));

        return FriendResponse.FriendDTO.builder()
            .friendId(user.getUserId())
            .friendName(user.getUserName())
            .friendStatus(user.getUserStatus())
            .build();
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        Friend findFriend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        friendRepository.delete(findFriend);
    }

    @Override
    public List<Friend> findAllByUserId(String userId) {
        return friendRepository.findAllByUserId(userId);
    }
}

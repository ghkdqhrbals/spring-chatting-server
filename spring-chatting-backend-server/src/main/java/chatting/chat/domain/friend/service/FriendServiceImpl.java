package chatting.chat.domain.friend.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.web.error.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static chatting.chat.web.error.ErrorCode.*;


@Service
@Transactional
public class FriendServiceImpl implements FriendService{

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendServiceImpl(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Friend save(String userId, String friendId){
        // validation
        Optional<User> findUser = userRepository.findById(userId);
        Optional<User> findUserFriend = userRepository.findById(friendId);

        if (!findUser.isPresent() || !findUserFriend.isPresent()){
            throw new CustomException(CANNOT_FIND_USER);
        }

        Friend isFriend1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
        Friend isFriend2 = friendRepository.findByUserIdAndFriendId(friendId, userId);

        if (userId.equals(friendId)){
            throw new CustomException(DUPLICATE_FRIEND_SELF);
        }

        // 이미 나와 친구임
        if (isFriend1 != null){
            throw new CustomException(DUPLICATE_FRIEND);
        }

        // logic
        Friend friend1 = new Friend(findUser.get(),findUserFriend.get().getUserId());
        Friend friend2 = new Friend(findUserFriend.get(),findUser.get().getUserId());

        Friend save = friendRepository.save(friend1);

        if (isFriend2 == null){
            friendRepository.save(friend2); // 상대방또한 나를 친구추가
        }
        return save;
    }

    @Override
    public void removeFriend(String userId, String friendId){
        Friend findFriend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        friendRepository.delete(findFriend);
    }

    @Override
    public List<Friend> findAllByUserId(String userId) {
        return friendRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<Friend> findByUserAndFriend(User user, Friend friend) {
        return Optional.empty();
    }

    @Override
    public List<Friend> findAll(User user) {
        return friendRepository.findAllByUserId(user.getUserId());
    }




}

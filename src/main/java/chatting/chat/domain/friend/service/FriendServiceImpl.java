package chatting.chat.domain.friend.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.web.error.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static chatting.chat.web.error.ErrorCode.DUPLICATE_FRIEND;
import static chatting.chat.web.error.ErrorCode.DUPLICATE_FRIEND_SELF;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class FriendServiceImpl implements FriendService{

    private final FriendRepository friendRepository;

    @Autowired
    public FriendServiceImpl(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @Override
    public Friend save(String userId, String friendId){

        Friend friend1 = new Friend();
        friend1.setUserId(userId);
        friend1.setFriendId(friendId);

        Friend friend2 = new Friend();
        friend2.setUserId(friendId);
        friend2.setFriendId(userId);

        Friend isFriend1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
        Friend isFriend2 = friendRepository.findByUserIdAndFriendId(friendId, userId);

        if (userId.equals(friendId)){
            throw new CustomException(DUPLICATE_FRIEND_SELF);
        }

        if (isFriend1 != null){
            // 이미 나와 친구임
            throw new CustomException(DUPLICATE_FRIEND);
        }

        Friend save1 = friendRepository.save(friend1);

        if (isFriend2 == null){
            // 상대방또한 나를 친구추가
            Friend save2 = friendRepository.save(friend2);
        }
        return save1;
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

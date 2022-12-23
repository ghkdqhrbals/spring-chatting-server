package chatting.chat.domain.friend.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

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
    public String save(String userId, String friendId){

        Friend friend1 = new Friend();
        friend1.setFriendId(friendId);
        friend1.setUserId(userId);


        Friend friend2 = new Friend();
        friend2.setFriendId(userId);
        friend2.setUserId(friendId);

        Friend isFriend1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
        Friend isFriend2 = friendRepository.findByUserIdAndFriendId(friendId, userId);

        if (isFriend1 != null){
            return "1"; // 내가 이미 친구추가함
        }

        if (isFriend2 != null){
            return "2"; // 상대방이 이미 친구추가함
        }

        Friend save1 = friendRepository.save(friend1);
        Friend save2 = friendRepository.save(friend2);

        if (save1 == null || save2 == null){
            return "3"; // 저장에러
        }

        return "0"; // 성공
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

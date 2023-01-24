package chatting.chat.domain.user.service;

import chatting.chat.domain.data.User;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.web.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static chatting.chat.web.error.ErrorCode.*;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 유저 검색
    @Override
    public User findById(String id) {
        Optional<User> findId = userRepository.findById(id);
        if (!findId.isPresent()){
            throw new CustomException(CANNOT_FIND_USER);
        }
        return findId.get();
    }

    // 유저 저장
    @Override
    public User save(User user) {
        Optional<User> findUser = userRepository.findById(user.getUserId());

        if (findUser.isPresent()) {
            throw new CustomException(DUPLICATE_RESOURCE);
        }
        User save = userRepository.save(user);
        return save;
    }

    // 로그인
    @Override
    public User login(String userId, String userPw) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            if (findUser.get().getUserPw().equals(userPw)){
                findUser.get().setLoginDate(LocalDate.now());
                return findUser.get();
            }
            throw new CustomException(INVALID_PASSWORD);
        }
        throw new CustomException(CANNOT_FIND_USER);
    }

    // 로그아웃
    @Override
    public void logout(String userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            findUser.get().setLogoutDate(LocalDate.now());
        }else{
            throw new CustomException(CANNOT_FIND_USER);
        }
    }

    // 유저 삭제
    @Override
    public void removeUser(String userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            userRepository.delete(findUser.get());
            return;
        }
        throw new CustomException(CANNOT_FIND_USER);
    }

    // 유저 업데이트
    @Override
    public void updateUser(User user) {
        Optional<User> findUser = userRepository.findById(user.getUserId());
        if (findUser.isPresent()){
            findUser.get().setUserName(user.getUserName());
            findUser.get().setUserId(user.getUserId());
            findUser.get().setUserPw(user.getUserPw());
            findUser.get().setEmail(user.getEmail());
        }
        throw new CustomException(CANNOT_FIND_USER);
    }

}

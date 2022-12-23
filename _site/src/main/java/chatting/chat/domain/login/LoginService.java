package chatting.chat.domain.login;

import chatting.chat.domain.data.User;
import chatting.chat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    public User login(String loginId, String password){
        User user = userRepository.findById(loginId)
                .filter(u -> u.getUserPw().equals(password))
                .orElse(null);
        if (user == null){
            return null;
        }

        user.setLoginDate(LocalDate.now());
        return user;
    }

    public Optional<User> logout(User user){
        Optional<User> findUser = userRepository.findById(user.getUserId());
        if (findUser == null){
            return null;
        }
        findUser.get().setLogoutDate(LocalDate.now());
        return Optional.ofNullable(user);
    }
}

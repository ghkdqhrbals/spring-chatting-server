package chatting.chat.domain.user.service;

import chatting.chat.domain.data.User;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


public interface UserService {
    CompletableFuture<?> save(User item);
    User findById(String id);
    User login(String userId, String userPw);
    void logout(String userId);
    void removeUser(String userId);
    void updateUser(User user);

}

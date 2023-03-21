package chatting.chat.domain.user.service;

import chatting.chat.domain.data.User;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


public interface UserService {
    CompletableFuture<?> save(User item);
    User findById(String id);
    DeferredResult<ResponseEntity<?>> login(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    DeferredResult<ResponseEntity<?>> logout(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    void removeUser(String userId);
    void updateUser(User user);

}

package chatting.chat.domain.user.api;

import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.dto.RequestUser;
import chatting.chat.web.dto.ResponseGetUser;
import chatting.chat.web.filter.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    
    @GetMapping("/")
    @Operation(summary = "Get user information")
    public ResponseEntity<?> findUser() {
        User findUser = userService.findById(UserContext.getUserId());
        return ResponseEntity.ok(new ResponseGetUser(findUser.getUserId(), findUser.getUserName(),
            findUser.getUserStatus()));
    }

    @PostMapping("/")
    @Operation(summary = "Save user")
    public ResponseEntity<?> addUser(@RequestBody RequestUser req) {
        log.trace(req.toString());
        return ResponseEntity.ok(userService.save(req.getUserId(), req.getUserName(), ""));
    }

}

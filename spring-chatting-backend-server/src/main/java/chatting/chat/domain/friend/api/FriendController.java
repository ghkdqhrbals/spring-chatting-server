package chatting.chat.domain.friend.api;

import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.filter.UserContext;
import com.example.commondto.dto.friend.FriendRequest;
import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.dto.friend.FriendResponse.FriendDTO;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class FriendController {
    private final UserContext userContext;

    private final UserService userService;
    private final FriendService friendService;

    @GetMapping("/friend")
    @Operation(summary = "Get a friend information that connected with user")
    public ResponseEntity<FriendResponse.FriendDTO> findMyFriend(@RequestParam("friendId") String friendId) {
        return ResponseEntity.ok(friendService.findMyFriend(userContext.getUserId(),friendId));
    }

    @GetMapping("/friends")
    @Operation(summary = "Get friends information that connected with user")
    public ResponseEntity<List<FriendResponse.FriendDTO>> findFriends() {
        return ResponseEntity.ok(userService.findAllFriends(userContext.getUserId()));
    }

    @PostMapping("/friend")
    @Operation(summary = "Add friends")
    public ResponseEntity<FriendResponse.FriendDTO> addFriend(@RequestBody FriendRequest.NewFriendDTO newFriendDTO) {
        return ResponseEntity.ok(friendService.save(userContext.getUserId(), newFriendDTO.getFriendId()));
    }

    @DeleteMapping("/friend")
    @Operation(summary = "Remove friends")
    public ResponseEntity<?> removeFriend(@RequestParam("userId") String userId,
        @RequestParam("friendId") String friendId) {
        friendService.removeFriend(userId, friendId);
        return ResponseEntity.ok("success");
    }
}

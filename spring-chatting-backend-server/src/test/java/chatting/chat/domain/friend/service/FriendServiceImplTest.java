package chatting.chat.domain.friend.service;

import static org.junit.jupiter.api.Assertions.*;

import chatting.chat.domain.user.entity.User;
import chatting.chat.initial.Initializer;
import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.error.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = { "spring.datasource.url=jdbc:tc:postgresql:11-alpine:///databasename" }
)
@ActiveProfiles("test")
class FriendServiceImplTest extends Initializer {

    private final User testUser = User.builder()
        .userId("userId")
        .userName("userName")
        .userStatus("").build();

    private final User testFriendUser = User.builder()
        .userId("friendId")
        .userName("friendName")
        .userStatus("").build();

    @BeforeEach
    void setUpRemove() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자가 다른 사용자를 친구로 성공적으로 추가")
    public void whenSaveFriendWithValidUserIds_thenFriendShouldBeAdded() {
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());

        // when
        FriendResponse.FriendDTO savedFriend = friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // then
        assertNotNull(savedFriend);
        assertEquals(testFriendUser.getUserId(), savedFriend.getFriendId());
    }

    @Test
    @DisplayName("사용자가 자기 자신을 친구로 추가하려 할 때 예외 발생")
    public void whenSaveFriendWithSameUserIds_thenExceptionShouldBeThrown() {
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());

        // when + then
        assertThrows(CustomException.class, () -> {
            friendService.save(testUser.getUserId(), testUser.getUserId());
        });
    }

    @Test
    @DisplayName("사용자가 특정 친구의 정보를 성공적으로 조회")
    public void whenFindMyFriendWithValidIds_thenFriendInfoShouldBeReturned() {
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // when
        FriendResponse.FriendDTO friendInfo = friendService.findMyFriend(testUser.getUserId(), testFriendUser.getUserId());

        // then
        assertNotNull(friendInfo);
        assertEquals(testFriendUser.getUserId(), friendInfo.getFriendId());
    }

    @Test
    @DisplayName("존재하지 않는 친구 ID로 친구 정보를 조회하려 할 때 예외 발생")
    public void whenFindMyFriendWithInvalidFriendId_thenExceptionShouldBeThrown() {
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        String invalidFriendId = "nonExistentUserId";

        // when + then
        assertThrows(CustomException.class, () -> {
            friendService.findMyFriend(testUser.getUserId(), invalidFriendId);
        });
    }

    @Test
    @DisplayName("사용자가 친구를 성공적으로 제거")
    public void whenRemoveFriend_thenFriendShouldBeRemoved() {
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        userService.save(testFriendUser.getUserId(), testFriendUser.getUserName(), testFriendUser.getUserStatus());
        friendService.save(testUser.getUserId(), testFriendUser.getUserId());

        // when
        friendService.removeFriend(testUser.getUserId(), testFriendUser.getUserId());

        // then
        assertFalse(friendService.areFriends(testUser.getUserId(), testFriendUser.getUserId()));
    }

    @Test
    @DisplayName("존재하지 않는 친구 ID로 친구를 제거하려 해도 성공적으로 제거")
    public void whenRemoveFriendWithInvalidFriendId_thenExceptionShouldBeThrown() {
        // given
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        String invalidFriendId = "nonExistentUserId";

        // when
        friendService.removeFriend(testUser.getUserId(), invalidFriendId);

        // then
        assertFalse(friendService.areFriends(testUser.getUserId(), invalidFriendId));

    }
}
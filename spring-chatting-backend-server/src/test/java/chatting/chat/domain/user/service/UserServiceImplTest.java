package chatting.chat.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;

import chatting.chat.domain.friend.entity.Friend;
import chatting.chat.domain.user.entity.User;
import chatting.chat.initial.Initializer;
import chatting.chat.web.kafka.dto.RequestChangeUserStatusDTO;
import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.error.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"spring.datasource.url=jdbc:tc:postgresql:11-alpine:///databasename"}
)
@ActiveProfiles("test")
class UserServiceImplTest extends Initializer {

    private final User testUser = User.builder()
        .userId("test")
        .userName("test")
        .userStatus("").build();

    @BeforeEach
    void setUpRemove() {
        userRepository.delete(testUser);
    }

    @Test
    @DisplayName("유효한 사용자 ID로 사용자를 찾으면 사용자 정보를 반환")
    public void whenFindByIdWithValidId_thenUserShouldBeFound() {
        // given
        User testUser = User.builder().userId("validUserId").build();
        userRepository.save(testUser);

        // when
        User found = userService.findById(testUser.getUserId());

        // then
        assertNotNull(found);
        assertEquals(testUser.getUserId(), found.getUserId());

        // cleanup
        userRepository.delete(testUser);
    }

    @Test
    @DisplayName("유효한 사용자 ID로 친구 목록을 조회하면 친구 목록 반환")
    public void whenFindAllFriendsWithValidUserId_thenFriendsListShouldBeReturned() {
        // given
        userRepository.save(testUser);
        User friend = User.builder().userName("myfriend").userId("friendId").build();
        userRepository.save(friend);

        Friend testFriend = Friend.builder().user(testUser).friendId("friendId").build();
        friendRepository.save(testFriend);

        // when
        List<FriendResponse.FriendDTO> friends = userService.findAllFriends(testUser.getUserId());

        // then
        assertNotNull(friends);
        assertFalse(friends.isEmpty());
        assertEquals("friendId", friends.get(0).getFriendId());

        // cleanup
        friendRepository.delete(testFriend);
        userRepository.delete(testUser);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 사용자를 찾으면 예외 발생")
    public void whenFindAllFriendsWithInvalidUserId_thenExceptionShouldBeThrown() {
        // given
        String userId = "nonExistentUserId";

        // when + then
        assertThrows(CustomException.class, () -> {
            userService.findAllFriends(userId);
        });
    }

    @Test
    @DisplayName("새로운 사용자를 성공적으로 저장하면 사용자 정보 반환")
    public void whenSaveNewUser_thenUserShouldBeSavedAndReturned() {
        // given
        String userId = "newUserId";
        String userName = "newUserName";
        String userStatus = "newUserStatus";

        // when
        User savedUser = userService.save(userId, userName, userStatus);

        // then
        assertNotNull(savedUser);
        assertEquals(userId, savedUser.getUserId());
        assertEquals(userName, savedUser.getUserName());
        assertEquals(userStatus, savedUser.getUserStatus());
    }

    @Test
    @DisplayName("이미 존재하는 사용자 ID로 새 사용자를 저장하려 하면 예외 발생")
    public void whenSaveUserWithExistingId_thenExceptionShouldBeThrown() {
        // given
        userRepository.save(testUser);

        // when + then
        assertThrows(CustomException.class, () -> {
            userService.save(testUser.getUserId(), "newUserName", "newUserStatus");
        });
    }


    @Test
    @DisplayName("존재하지 않는 사용자 ID로 친구 목록을 조회하면 예외 발생")
    public void whenFindByIdWithInvalidId_thenExceptionShouldBeThrown() {
        // given
        String userId = "nonExistentUserId";

        // when + then
        assertThrows(CustomException.class, () -> {
            userService.findById(userId);
        });
    }


    @Test
    @DisplayName("사용자 상태 메시지를 성공적으로 업데이트하면 변경된 상태 확인")
    public void whenUpdateUserStatusWithValidId_thenStatusShouldBeUpdated() {
        // given
        RequestChangeUserStatusDTO req = new RequestChangeUserStatusDTO();
        userService.save(testUser.getUserId(), testUser.getUserName(), testUser.getUserStatus());
        req.setUserId(testUser.getUserId());
        req.setStatus("Updated Status");

        // when
        userService.updateUserStatus(req);

        // then
        User updatedUser = userRepository.findById(testUser.getUserId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated Status", updatedUser.getUserStatus());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 상태 메시지 업데이트 시 예외 발생")
    public void whenUpdateUserStatusWithInvalidId_thenExceptionShouldBeThrown() {
        // given
        RequestChangeUserStatusDTO req = new RequestChangeUserStatusDTO();
        req.setUserId("nonExistentUserId");
        req.setStatus("Updated Status");

        // when + then
        assertThrows(CustomException.class, () -> {
            userService.updateUserStatus(req);
        });
    }

    @Test
    @DisplayName("지정된 사용자 ID의 사용자를 성공적으로 제거")
    public void whenRemoveUserWithValidId_thenUserShouldBeRemoved() {
        // given
        userRepository.save(testUser);

        // when
        userService.remove(testUser.getUserId());

        // then
        Optional<User> deletedUser = userRepository.findById(testUser.getUserId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 사용자를 제거하려 할 때 예외 발생")
    public void whenRemoveUserWithInvalidId_thenExceptionShouldBeThrown() {
        // given
        String userId = "nonExistentUserId";

        // when + then
        assertThrows(CustomException.class, () -> {
            userService.remove(userId);
        });
    }


}
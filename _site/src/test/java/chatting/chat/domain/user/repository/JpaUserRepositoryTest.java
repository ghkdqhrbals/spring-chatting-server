package chatting.chat.domain.user.repository;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.User;
import chatting.chat.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Re-create database
class JpaUserRepositoryTest {
    @Autowired
    private UserService userService;

    @BeforeEach
    void init() {
    }

    @AfterEach
    void afterEach() {
        log.info("afterEach:executed");
//        userService.resetDatabase();
    }

    @DisplayName("유저추가")
    @Test
    void addUser() {
        // Databse 초기화가 필요함
        User user1 = new User();
        user1.setUserId("user1");
        user1.setUserPw("1");
        user1.setUserName("user1");
        user1.setEmail("GMAIL.COM");
        user1.setLoginDate(LocalDate.now());
        user1.setJoinDate(LocalDate.now());
        user1.setLogoutDate(LocalDate.now());

        userService.save(user1);

        // logic
        Optional<User> findUser = userService.findById("user1");

        // validation
        Assertions.assertThat(findUser).isNotEmpty();
        Assertions.assertThat(findUser.get().getUserId()).isEqualTo(user1.getUserId());
        Assertions.assertThat(findUser.get().getUserName()).isEqualTo(user1.getUserName());
        Assertions.assertThat(findUser.get().getEmail()).isEqualTo(user1.getEmail());
        Assertions.assertThat(findUser.get().getJoinDate()).isEqualTo(user1.getJoinDate());
        Assertions.assertThat(findUser.get().getLoginDate()).isEqualTo(user1.getLoginDate());
        Assertions.assertThat(findUser.get().getLogoutDate()).isEqualTo(user1.getLogoutDate());
    }



}
package chatting.chat.domain.friend.service;

import chatting.chat.config.JpaConfig;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Re-create database
class FriendServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @BeforeEach
    void init() {
    }

    @AfterEach
    void afterEach() {
//        userService.resetDatabase();
//        friendService.resetDatabase();
    }


    @DisplayName("친구목록 받아오기")
    @Test
    void findMyFriends(){
        // Databse 초기화가 필요함
        User user1 = new User();
        user1.setUserId("user1");
        user1.setUserPw("1");
        user1.setUserName("user1");
        user1.setEmail("GMAIL.COM");
        user1.setLoginDate(LocalDate.now());
        user1.setJoinDate(LocalDate.now());
        user1.setLogoutDate(LocalDate.now());

        User user2 = new User();
        user2.setUserId("user2");
        user2.setUserPw("1");
        user2.setUserName("user2");
        user2.setEmail("GMAIL.COM");
        user2.setLoginDate(LocalDate.now());
        user2.setJoinDate(LocalDate.now());
        user2.setLogoutDate(LocalDate.now());

        User user3 = new User();
        user3.setUserId("user3");
        user3.setUserPw("1");
        user3.setUserName("user3");
        user3.setEmail("GMAIL.COM");
        user3.setLoginDate(LocalDate.now());
        user3.setJoinDate(LocalDate.now());
        user3.setLogoutDate(LocalDate.now());

        userService.save(user1);
        userService.save(user2);
        userService.save(user3);

        Friend friend1 = new Friend();
        friend1.setMe(user1);
        friend1.setFriendId(user2.getUserId());
        friendService.save(friend1);

        Friend friend2 = new Friend();
        friend2.setMe(user1);
        friend2.setFriendId(user3.getUserId());
        friendService.save(friend2);

        /**
         * Logic
         */
        List<Friend> findFriends = friendService.findAll(user1);

        /**
         * Validation
         */

        // NULL 확인
        Assertions.assertThat(findFriends).isNotEmpty();

        // 친구 두 명 확인
        Assertions.assertThat(findFriends).hasSize(2);

        for(Friend friend : findFriends ){
            log.info(friend.getFriendId());
        }
    }

    @DisplayName("친구추가하기")
    @Test
    void addFriend() {
        User user1 = new User();
        user1.setUserId("user1");
        user1.setUserPw("1");
        user1.setUserName("user1");
        user1.setEmail("GMAIL.COM");
        user1.setLoginDate(LocalDate.now());
        user1.setJoinDate(LocalDate.now());
        user1.setLogoutDate(LocalDate.now());

        User user2 = new User("user2","1","gmail.com","user2",LocalDate.now(),LocalDate.now(),LocalDate.now());

        userService.save(user1);
        userService.save(user2);
        
        // logic
        Friend friend1 = new Friend();
        friend1.setMe(user1);
        friend1.setFriendId(user2.getUserId());
        friendService.save(friend1);
        
        Optional<Friend> findFriend = friendService.findByUserAndFriend(user1, friend1);

        // validation
        Assertions.assertThat(findFriend.get()).isNotNull();
        Assertions.assertThat(findFriend.get().getFriendId()).isEqualTo(user2.getUserId());

        System.out.println("findFriend.get().getFriendId() = " + findFriend.get().getFriendId());

    }

    @DisplayName("findByUserAndFriend")
    @Test
    void findByUserAndFriend() {
        // 유저 받아오기
        User user1 = new User();
        user1.setUserId("user1");
        user1.setUserPw("1");
        user1.setUserName("user1");
        user1.setEmail("GMAIL.COM");
        user1.setLoginDate(LocalDate.now());
        user1.setJoinDate(LocalDate.now());
        user1.setLogoutDate(LocalDate.now());

        User user2 = new User();
        user2.setUserId("user2");
        user2.setUserPw("1");
        user2.setUserName("user2");
        user2.setEmail("GMAIL.COM");
        user2.setLoginDate(LocalDate.now());
        user2.setJoinDate(LocalDate.now());
        user2.setLogoutDate(LocalDate.now());

        User user3 = new User();
        user3.setUserId("user3");
        user3.setUserPw("1");
        user3.setUserName("user3");
        user3.setEmail("GMAIL.COM");
        user3.setLoginDate(LocalDate.now());
        user3.setJoinDate(LocalDate.now());
        user3.setLogoutDate(LocalDate.now());

        userService.save(user1);
        userService.save(user2);
        userService.save(user3);
        log.info("Successfully save {},{},{}",user1.getUserId(), user2.getUserId(), user3.getUserId());


        /**
         * Logic
         */
        Friend friend1 = new Friend();
        friend1.setMe(user1);
        friend1.setFriendId(user2.getUserId());
        friendService.save(friend1);

        Friend friend2 = new Friend();
        friend2.setMe(user1);
        friend2.setFriendId(user3.getUserId());
        friendService.save(friend2);

        /**
         * Validation
         */

        // Friend 받아오기
        Optional<Friend> findFriend1 = friendService.findByUserAndFriend(user1, friend1);
        Optional<Friend> findFriend2 = friendService.findByUserAndFriend(user1, friend2);

        // 친구가 Null인지 확인
        Assertions.assertThat(findFriend1.get()).isNotNull();
        Assertions.assertThat(findFriend2.get()).isNotNull();

        Assertions.assertThat(findFriend1.get()).isInstanceOf(Friend.class);
        Assertions.assertThat(findFriend2.get()).isInstanceOf(Friend.class);


        // 친구 ID 확인
        Assertions.assertThat(findFriend1.get().getFriendId()).isEqualTo(friend1.getFriendId());
        Assertions.assertThat(findFriend2.get().getFriendId()).isEqualTo(friend2.getFriendId());

        // 사용자 확인
        Assertions.assertThat(findFriend1.get().getMe().getUserId()).isEqualTo(user1.getUserId());
        Assertions.assertThat(findFriend1.get().getMe().getUserName()).isEqualTo(user1.getUserName());

        Assertions.assertThat(findFriend2.get().getMe().getUserId()).isEqualTo(user1.getUserId());
        Assertions.assertThat(findFriend2.get().getMe().getUserName()).isEqualTo(user1.getUserName());
    }
}
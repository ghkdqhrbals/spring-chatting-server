package com.example.shopuserservice.domain.user.repository;

import com.example.commondto.format.DateFormat;
import com.example.shopuserservice.UnitTest;
import com.example.shopuserservice.domain.data.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserRepositoryTest")
class UserRepositoryTest extends UnitTest {

    @Nested
    @DisplayName("Save User")
    class insertUserBatch{
        @Test
        @DisplayName("Saving single user")
        void saveUser(){
            // given
            LocalDateTime now = DateFormat.getCurrentTime();
            User user = User.builder()
                    .userId("aa")
                    .userPw("1234")
                    .userName("Hwang")
                    .role("USER_ROLE")
                    .email("hwang@gmail.com")
                    .loginDate(now)
                    .logoutDate(now)
                    .build();

            // when
            user = userRepository.save(user);   // re-initializing

            // then
            User savedUser = userRepository.findById("aa").orElseThrow(RuntimeException::new);
            assertThat(savedUser.getUserPw()).isEqualTo(user.getUserPw());
            assertThat(savedUser.getUserName()).isEqualTo(user.getUserName());
            assertThat(savedUser.getRole()).isEqualTo(user.getRole());
            assertThat(savedUser.getLogoutDate()).isEqualTo(user.getLogoutDate());
            assertThat(savedUser.getLoginDate()).isEqualTo(user.getLoginDate());
            assertThat(savedUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
        }

        @Test
        @DisplayName("Saving single user")
        void saveUserList(){
            // given
            LocalDateTime now = LocalDateTime.now();
            User user = User.builder()
                    .userId("aa")
                    .userPw("1234")
                    .userName("Hwang")
                    .role("USER_ROLE")
                    .email("hwang@gmail.com")
                    .loginDate(now)
                    .logoutDate(now)
                    .build();

            // when
            user = userRepository.save(user);   // re-initializing

            // then
            User savedUser = userRepository.findById("aa").orElseThrow(RuntimeException::new);
            assertThat(savedUser.getUserPw()).isEqualTo(user.getUserPw());
            assertThat(savedUser.getUserName()).isEqualTo(user.getUserName());
            assertThat(savedUser.getRole()).isEqualTo(user.getRole());
            assertThat(savedUser.getLogoutDate()).isEqualTo(user.getLogoutDate());
            assertThat(savedUser.getLoginDate()).isEqualTo(user.getLoginDate());
            assertThat(savedUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
        }
    }
}
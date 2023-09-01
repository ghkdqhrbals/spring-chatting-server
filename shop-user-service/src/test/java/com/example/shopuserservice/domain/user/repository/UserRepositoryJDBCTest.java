package com.example.shopuserservice.domain.user.repository;

import com.example.shopuserservice.UnitTest;
import com.example.shopuserservice.domain.data.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("유저 삽입 배치 테스트")
class UserRepositoryJDBCTest extends UnitTest {

    @Nested
    @DisplayName("유저 저장")
    class insertUserBatch{
        @Test
        @DisplayName("단일 저장")
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
                    .joinDate(now)
                    .build();

            // when
            userRepositoryJDBC.saveAll(Arrays.asList(user));

            // then
            User savedUser = userRepository.findById("aa").orElseThrow(RuntimeException::new);
            assertThat(savedUser.getUserPw()).isEqualTo(user.getUserPw());
            assertThat(savedUser.getUserName()).isEqualTo(user.getUserName());
            assertThat(savedUser.getRole()).isEqualTo(user.getRole());

            // TODO
            assertThat(Timestamp.valueOf(savedUser.getLogoutDate())).isEqualTo(Timestamp.valueOf(user.getLogoutDate()));
            assertThat(Timestamp.valueOf(savedUser.getLoginDate())).isEqualTo(Timestamp.valueOf(user.getLoginDate()));
            assertThat(Timestamp.valueOf(savedUser.getJoinDate())).isEqualTo(Timestamp.valueOf(user.getJoinDate()));

        }
    }
}
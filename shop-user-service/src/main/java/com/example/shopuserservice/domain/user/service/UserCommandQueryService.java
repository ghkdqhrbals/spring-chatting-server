package com.example.shopuserservice.domain.user.service;

import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.shopuserservice.domain.user.data.User;
import com.example.shopuserservice.domain.user.data.UserTransactions;
import com.example.commondto.dto.user.UserDto;
import com.example.shopuserservice.web.vo.RequestUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.*;


/**
 * UserCommandQueryService 는 CQRS의 CQR의 역할을 수행하는 서비스입니다.
 * 본 서비스는 별도의 Transaction DB에 따라 관리되며, DB를 수정할 수 있는 권한을 가지고 있습니다.
 *
 */
public interface UserCommandQueryService {
    CompletableFuture<List<User>> getAllUser();
    CompletableFuture<User> createUser(RequestUser request, UUID eventId);
    CompletableFuture<UserTransactions> newUserEvent(RequestUser request, UUID eventId, UserEvent userEvent);
    CompletableFuture<Optional<User>> getUserById(String id);
    CompletableFuture<UserTransactions> updateStatus(UserResponseEvent event);
    DeferredResult<ResponseEntity<?>> login(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    DeferredResult<ResponseEntity<?>> logout(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    void updateUser(User user);
    CompletableFuture<UserDto> getUserDetailsByUserId(String username);
    CompletableFuture<String> changePassword(String userId, String userPw);
}

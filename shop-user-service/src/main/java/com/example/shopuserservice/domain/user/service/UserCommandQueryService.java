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

public interface UserCommandQueryService {
    CompletableFuture<List<User>> getAllUser();
    CompletableFuture<User> createUser(RequestUser request, UUID eventId);
    CompletableFuture<UserTransactions> newUserEvent(RequestUser request, UUID eventId, UserEvent userEvent);
    CompletableFuture<Optional<User>> getUserById(String id);
    CompletableFuture<UserTransactions> updateStatus(UserResponseEvent event);
    void updateUser(User user);
    CompletableFuture<UserDto> getUserDetailsByUserId(String username);
    CompletableFuture<String> changePassword(String userId, String userPw);
}

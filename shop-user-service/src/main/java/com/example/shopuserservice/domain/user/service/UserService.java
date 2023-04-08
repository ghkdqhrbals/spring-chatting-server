package com.example.shopuserservice.domain.user.service;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.web.dto.UserDto;
import com.example.shopuserservice.web.vo.RequestUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.*;

public interface UserService {
    CompletableFuture<List<User>> getAllUser();
    CompletableFuture<User> createUser(RequestUser request);
    CompletableFuture<Optional<User>> getUserById(String id);
    DeferredResult<ResponseEntity<?>> login(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    DeferredResult<ResponseEntity<?>> logout(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    void removeUser(String userId);
    void updateUser(User user);


    CompletableFuture<UserDto> getUserDetailsByUserId(String username);
}

package com.example.shopuserservice.domain.user.service;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.web.dto.UserDto;
import com.example.shopuserservice.web.vo.RequestUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.*;

public interface UserService extends UserDetailsService {
    CompletableFuture<User> createUser(RequestUser request);
    CompletableFuture<List<User>> getUserById(String id);
    DeferredResult<ResponseEntity<?>> login(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    DeferredResult<ResponseEntity<?>> logout(String userId, String userPw, DeferredResult<ResponseEntity<?>> dr);
    void removeUser(String userId);
    void updateUser(User user);

    CompletableFuture<UserDto> getUserDetailsByUserId(String username);
}

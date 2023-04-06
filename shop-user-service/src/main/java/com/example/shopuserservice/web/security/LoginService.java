package com.example.shopuserservice.web.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtTokenProvider jwtTokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;


    public Mono<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        if (loginRequestDto.getPassword() == null || loginRequestDto.getUsername() == null)
            return Mono.error(new ServerWebInputException("User Input Invalidation"));


        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                loginRequestDto.getPassword());
        log.info(authentication.getPrincipal().toString());
        return authenticationManager.authenticate(authentication)
                .map(jwtTokenProvider::createToken)
                .map(token -> LoginResponseDto.builder().token(token).build());
    }
}
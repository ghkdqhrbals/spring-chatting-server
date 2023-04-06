//package com.example.shopuserservice.web.security;
//
//import com.example.shopuserservice.domain.user.service.UserService;
//import com.example.shopuserservice.web.dto.UserDto;
//import com.example.shopuserservice.web.vo.RequestLogin;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.env.Environment;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//@Slf4j
//public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private UserService userService;
//    private Environment env;
//
//    @Value("${token.expiration_time}")
//    Long expirationTime;
//
//    @Value("${token.secret}")
//    String secret;
//
//    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
//        super.setAuthenticationManager(authenticationManager);
//        this.userService = userService;
//        this.env = env;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request,
//                                                HttpServletResponse response) throws AuthenticationException {
//        try {
//            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(),RequestLogin.class);
//            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(creds.getUserId(),creds.getUserPw());
//            setDetails(request, authRequest);
//            return this.getAuthenticationManager().authenticate(authRequest);
////            Arrays.asList(new SimpleGrantedAuthority("USER"))
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request,
//                                            HttpServletResponse response,
//                                            FilterChain chain,
//                                            Authentication authResult) throws IOException, ServletException {
//        String username = ((User) authResult.getPrincipal()).getUsername();
//        UserDto userDto = null;
//
//        CompletableFuture<UserDto> cf = userService.getUserDetailsByUserId(username);
//        try {
//            userDto = cf.get();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        Date date = new Date(System.currentTimeMillis() + expirationTime);
//        String token = Jwts.builder()
//                .setSubject(userDto.getUserId())
//                .setExpiration(date)
//                .signWith(SignatureAlgorithm.HS512, secret)
//                .compact();
//
//        response.addHeader("token",token);
//        response.addHeader("userId",userDto.getUserId());
//
//    }
//}

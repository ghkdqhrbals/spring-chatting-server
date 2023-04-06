//package com.example.shopuserservice.web.security;
//
//import com.example.shopuserservice.domain.user.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.core.env.Environment;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//@Configuration
////@EnableWebSecurity
//@EnableWebFluxSecurity
//@Slf4j
//public class WebSecurity {
//
//    private Environment env;
//    private UserService userService;
//    private AuthenticationConfiguration authenticationConfiguration;
//
//    @Autowired
//    public void setUserService(@Lazy UserService userService) {
//        this.userService = userService;
//    }
//
//    @Autowired
//    public void setAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration) {
//        this.authenticationConfiguration = authenticationConfiguration;
//    }
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
//                                                                       PasswordEncoder passwordEncoder) {
//        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
//        authenticationManager.setPasswordEncoder(passwordEncoder);
//        return authenticationManager;
//    }
//
//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable();
//        http.authorizeHttpRequests().requestMatchers("/actuator/**").permitAll();
//        http.authorizeHttpRequests().requestMatchers("/**").permitAll();
//        http.addFilter(getAuthenticationFiler());
//
//        http.headers().frameOptions().disable();
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationFilter getAuthenticationFiler() throws Exception {
//        AuthenticationFilter authenticationFilter = new AuthenticationFilter(
//                authenticationConfiguration.getAuthenticationManager(), userService, env);
//        return authenticationFilter;
//    }
//}

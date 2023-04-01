package com.example.shopuserservice.web.security;

import com.example.shopuserservice.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurity {

    private Environment env;
    private UserService userService;
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests().requestMatchers("/actuator/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("/**").permitAll();
        http.addFilter(getAuthenticationFiler());

        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public AuthenticationFilter getAuthenticationFiler() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(
                authenticationConfiguration.getAuthenticationManager(), userService, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager());
        return authenticationFilter;
    }

//    @Bean
//    public AuthenticationFilter customAuthenticationFilter() throws Exception {
//        AuthenticationFilter customAuthenticationFilter = new AuthenticationFilter(authenticationManager(),userService,env);
//        customAuthenticationFilter.setFilterProcessesUrl("/user/login");
//        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
//        customAuthenticationFilter.afterPropertiesSet();
//        return customAuthenticationFilter;
//    }
//
//    @Bean
//    public CustomLoginSuccessHandler customLoginSuccessHandler() {
//        return new CustomLoginSuccessHandler();
//    }

}

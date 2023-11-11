package com.example.shopuserservice.web.security;

import com.example.shopuserservice.domain.user.data.User;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.web.security.filter.JwtRefreshTokenAuthenticationFilter;
import com.example.shopuserservice.web.security.filter.JwtTokenAuthenticationFilter;
import com.example.shopuserservice.web.security.token.UserRedisSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;


/**
 * 보안에 필요한 것들을 여기에 작성한다.
 * <p>
 * authentication manager, security context repository, 허용이 필요한 url등.
 * <p>
 *
 *  @EnableReactiveMethodSecurity은 DefaultMethodSecurityExpressionHandler클래스를 애플리케이션 컨텍스트에 등록해 준다.
 *  methodSecurityExpressionHandler
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ReactiveSecurityConfig {

    private final ApplicationContext applicationContext;
    private final UserRedisSessionRepository userRedisSessionRepository;

    /**
     * ServerHttpSecurity는 스프링 시큐리티의 HttpSecurity와 비슷한 웹플럭스용 클래스다.
     * 이 클래스를 이용하여 모든 요청에 대해 인증 여부 체크를 정의할 수 있다.
     * 이 클래스에 필터를 추가하여, 요청에 인증용 토큰이 존재할 경우 인증이 되도록 설정할 수 있다.
     *
     * SecurityWebFilterChain클래스를 생성하기 전에 DefaultMethodSecurityExpressionHandler클래스가 먼저 구성되어 있어야 한다.
     * <p>
     * authenticationEntryPoint: 애플리케이션이 인증을 요청할 때 해야 할 일들을 정의함.
     * accessDeniedHandler: 인증된 사용자가 필요한 권한을 가지고 있을 않을 때 헤야 할 일들을 정의함.
     *
     * @param http
     * @return
     */
    @Bean
//    @DependsOn({"methodSecurityExpressionHandler"})
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtTokenProvider jwtTokenProvider,
                                                         ReactiveAuthenticationManager reactiveAuthenticationManager) {
        DefaultMethodSecurityExpressionHandler defaultWebSecurityExpressionHandler = this.applicationContext.getBean(DefaultMethodSecurityExpressionHandler.class);
        defaultWebSecurityExpressionHandler.setPermissionEvaluator(myPermissionEvaluator());
        return http
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, ex) -> {
                            return Mono.fromRunnable(() -> {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            });
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            return Mono.fromRunnable(() -> {
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            });
                        }))
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                // authenticate
                .addFilterAt(new JwtTokenAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .addFilterAfter(new JwtRefreshTokenAuthenticationFilter(jwtTokenProvider,userRedisSessionRepository), SecurityWebFiltersOrder.HTTP_BASIC)
                .authorizeExchange(exchange -> exchange
                        // 승인 목록
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() // 사용가능 Method
                        .pathMatchers(HttpMethod.POST,"/user").permitAll() // 회원가입
                        .pathMatchers("/login").permitAll() // 로그인
                        // 권한 필터
                        .pathMatchers("/admin/**").hasRole("ADMIN") // admin 만 접근가능하도록 권한 설정
                        .pathMatchers("/**").hasAnyRole("USER","ADMIN") // 다른 모든 method 권한 설정
                        .anyExchange().authenticated()
                )
                .build();
    }

    @Bean
    public PermissionEvaluator myPermissionEvaluator() {
        return new PermissionEvaluator() {
            @Override
            public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
                log.info("has Permission?");
                if(authentication.getAuthorities().stream()
                        .filter(grantedAuthority -> {
                            log.info("My Authorities={}", grantedAuthority.getAuthority());
                            log.info("permission={}", permission);
                            return grantedAuthority.getAuthority().equals((String) permission);
                        })
                        .count() > 0) {
                    log.info("Yes Authorities={}", authentication.getAuthorities());
                    return true;
                }
                return false;
            }

            @Override
            public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
                return false;
            }
        };
    }



    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            log.info("Finduser : {}",username);
            Optional<User> findUser = userRepository.findById(username);
            if (!findUser.isPresent()) {
                return Mono.empty();
            }

            User user = findUser.get();

            CustomUserDetails userDetails = new CustomUserDetails();
            userDetails.setUsername(user.getUserId());
            userDetails.setPassword(user.getUserPw());
            userDetails.setEnabled(true);
            userDetails.setAccountNonExpired(true);
            userDetails.setCredentialsNonExpired(true);
            userDetails.setAccountNonLocked(true);
            userDetails.setPermissions(Arrays.asList(user.getRole()));
            return Mono.just(userDetails);
        };
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

}
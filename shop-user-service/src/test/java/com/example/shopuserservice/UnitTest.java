package com.example.shopuserservice;

import com.example.shopuserservice.domain.user.redisrepository.UserTransactionRedisRepository;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.repository.UserRepositoryJDBC;
import com.example.shopuserservice.domain.user.service.UserCommandQueryServiceImpl;
import com.example.shopuserservice.domain.user.service.UserReadService;
import com.example.shopuserservice.domain.user.service.UserReadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
public class UnitTest {
    @SpyBean
    protected UserRepositoryJDBC userRepositoryJDBC;
    @SpyBean
    protected UserRepository userRepository;
    @SpyBean
    protected UserTransactionRedisRepository userTransactionRedisRepository;
    @Autowired
    protected UserCommandQueryServiceImpl userCommandQueryService;
    @Autowired
    protected UserReadService userReadService;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();
        userTransactionRedisRepository.deleteAll();
    }
}

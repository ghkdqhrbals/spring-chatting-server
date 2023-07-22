package com.example.shopuserservice.web.logger;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DataSourceAspectLogger {

    private HikariPool pool;

    @Autowired
    private HikariDataSource ds;




    @Before("execution(* com.example.shopuserservice.domain.user.repository.UserRepositoryJDBC.saveAll(..))")
    public void logBeforeConnection(JoinPoint jp) throws Throwable {
//        logDataSourceInfos("Before ", jp);
    }

    @After("execution(* com.example.shopuserservice.domain.user.repository.UserRepositoryJDBC.saveAll(..))")
    public void logAfterConnection(JoinPoint jp) throws Throwable {
//        logDataSourceInfos("After ", jp);
    }

    @Before("execution(* com.example.shopuserservice.web.controller.UserController.*(..))")
    public void logBeforeController(JoinPoint jp) throws Throwable {
        log.info("BEFORE CONTROLLER ACCESS");
    }

    @After("execution(* com.example.shopuserservice.web.controller.UserController.*(..))")
    public void logAfterController(JoinPoint jp) throws Throwable {
        log.info("AFTER CONTROLLER ACCESS");
    }



    @Autowired
    public void getPool() {
        try {
            java.lang.reflect.Field field = ds.getClass().getDeclaredField("pool");
            field.setAccessible(true);
            this.pool = (HikariPool) field.get(ds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void logDataSourceInfos(final String time, final JoinPoint jp) {
        final String method = String.format("%s", jp.getSignature().getName());
        int totalConnections = pool.getTotalConnections();
        int activeConnections = pool.getActiveConnections();
        int freeConnections = totalConnections - activeConnections;
        int connectionWaiting = pool.getThreadsAwaitingConnection();
        log.info(String.format("%s %s: [Total: %d, Active: %d, Idle: %d, Wait: %d]", time, method, ds.getMaximumPoolSize(),activeConnections,freeConnections,connectionWaiting));
    }
}
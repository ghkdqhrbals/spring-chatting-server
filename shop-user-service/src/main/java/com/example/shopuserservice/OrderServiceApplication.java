package com.example.shopuserservice;

import com.example.shopuserservice.config.JpaConfig;
import feign.Logger;
import org.apache.http.client.methods.HttpTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.example",exclude={DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
@Import(JpaConfig.class)
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public Logger.Level feignLoggerLevel(){
		return Logger.Level.FULL;
	}
	@Bean
	public HttpExchangeRepository httpTraceRepository(){
		return new InMemoryHttpExchangeRepository();
	}

	@Bean(name = "bcrypt")
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}

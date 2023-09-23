package com.example.shopuserservice.domain.user.repository;


import com.example.shopuserservice.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String>{
    List<User> findAll();
}

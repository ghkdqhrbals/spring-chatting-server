package com.example.shopuserservice.domain.user.repository;


import com.example.shopuserservice.domain.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

//    @Query("select u from User u where u.userId = :userId")
//    List<User> findByUserId(@Param("userId") String userId);
//
//
    List<User> findAll();
}

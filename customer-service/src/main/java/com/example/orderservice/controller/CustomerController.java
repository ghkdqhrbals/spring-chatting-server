package com.example.orderservice.controller;

import com.example.orderservice.service.UserBalanceCommandQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class CustomerController {

    private UserBalanceCommandQueryService userBalanceCommandQueryService;

}

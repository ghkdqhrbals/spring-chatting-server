package com.example.shopuserservice.client;

import com.example.shopuserservice.web.vo.RequestOrder;
import com.example.shopuserservice.web.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/{userId}/orders")
    List<ResponseOrder>  getOrders(@PathVariable("userId") String userId);
    @PostMapping("/{userId}/orders")
    List<ResponseOrder>  createOrders(@PathVariable("userId") String userId, @RequestBody RequestOrder order);
}

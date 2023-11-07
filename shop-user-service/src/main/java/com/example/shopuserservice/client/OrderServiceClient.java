package com.example.shopuserservice.client;


import com.example.commondto.dto.order.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/{userId}/orders")
    List<ResponseOrder>  getOrders(@PathVariable("userId") String userId);

}

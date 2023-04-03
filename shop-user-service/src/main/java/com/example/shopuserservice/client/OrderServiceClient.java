package com.example.shopuserservice.client;

import com.example.shopuserservice.web.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/{userId}/orders_ng")
    List<ResponseOrder>  getOrders(@PathVariable("userId") String userId);
}

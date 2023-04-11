package com.example.orderservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto implements Serializable {
    private String productId;
    private String userId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
    private String orderId;
}
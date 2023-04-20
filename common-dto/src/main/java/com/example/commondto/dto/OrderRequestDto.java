package com.example.commondto.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderRequestDto {

    private Integer userId;
    private Integer productId;
    private Integer amount;
    private UUID orderId;
}
package com.example.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {
    private String productId;
    private String orderId;
    private Integer qty;
    private Integer totalPrice;
    private Integer unitPrice;
    private LocalDateTime createAt;
}

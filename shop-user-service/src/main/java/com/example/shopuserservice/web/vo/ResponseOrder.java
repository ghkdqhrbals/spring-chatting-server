package com.example.shopuserservice.web.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseOrder {
    private String productId;
    private String quantity;
    private Integer price;
    private Integer totalPrice;
    private Date createdAt;

    private String orderId;

}

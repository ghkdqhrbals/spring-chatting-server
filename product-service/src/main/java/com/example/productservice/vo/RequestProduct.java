package com.example.productservice.vo;

import lombok.Data;

@Data
public class RequestProduct {
    private String productId;
    private String productName;
    private Integer stock;
    private Integer price;
}

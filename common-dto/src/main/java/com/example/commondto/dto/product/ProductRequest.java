package com.example.commondto.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class ProductRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewProductDTO {

        private String name;
        private Integer price;
        private Integer stock;
    }
}

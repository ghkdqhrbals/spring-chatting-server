package com.example.commondto.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class OrderRequest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class NewOrderDTO {
        private UUID orderId;
        private Integer productId;
        private Integer price;
        private Integer amount;
        private Integer userId;
    }
}

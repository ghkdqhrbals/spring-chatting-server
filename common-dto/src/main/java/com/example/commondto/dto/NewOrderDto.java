package com.example.commondto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class NewOrderDto {

    private UUID orderId;
    private Integer productId;
    private Integer price;
    private Integer amount;
    private Integer userId;

}

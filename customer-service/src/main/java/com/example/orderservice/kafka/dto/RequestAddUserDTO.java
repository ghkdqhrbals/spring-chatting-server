package com.example.orderservice.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAddUserDTO {
    private String userId;
    private String userName;
}

package com.example.orderservice.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestAddUserRoomDTO {
    private String userId;
    private List<String> friendId;
}

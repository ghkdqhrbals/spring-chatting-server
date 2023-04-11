package com.example.productservice.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestAddFriendDTO {
    private String userId;
    private List<String> friendId;
}

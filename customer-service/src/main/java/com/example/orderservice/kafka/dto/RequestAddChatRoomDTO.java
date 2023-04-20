package com.example.orderservice.kafka.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestAddChatRoomDTO {
    private String userId;
    private List<String> friendIds;
}

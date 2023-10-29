package com.example.commondto.dto.friend;

import java.util.*;
import lombok.Builder;
import lombok.Data;

public class FriendRequest {

    @Data
    @Builder
    public static class NewFriendDTO {
        private String userId;
        private List<String> friendId;

    }

}

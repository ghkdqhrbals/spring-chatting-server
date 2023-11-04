package com.example.commondto.dto.friend;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class FriendRequest {
    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NewFriendDTO {
        @JsonProperty("friendId")
        private String friendId;
        @Builder
        public NewFriendDTO(String friendId) {
            this.friendId = friendId;
        }
    }
}

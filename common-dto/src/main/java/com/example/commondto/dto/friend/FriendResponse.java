package com.example.commondto.dto.friend;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class FriendResponse {
    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FriendDTO implements Serializable {
        @JsonProperty("friendId")
        private String friendId;
        @JsonProperty("friendName")
        private String friendName;
        @JsonProperty("friendStatus")
        private String friendStatus;

        @Builder
        public FriendDTO(String friendId, String friendName, String friendStatus) {
            this.friendId = friendId;
            this.friendName = friendName;
            this.friendStatus = friendStatus;
        }

    }

}

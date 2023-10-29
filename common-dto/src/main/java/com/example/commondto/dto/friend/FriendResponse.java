package com.example.commondto.dto.friend;

import lombok.Builder;
import lombok.Data;

public class FriendResponse {
    @Data
    @Builder
    public static class FriendDTO {
        private String friendId;
        private String friendName;
        private String friendStatus;

        public FriendDTO(String friendId, String friendName, String friendStatus) {
            this.friendId = friendId;
            this.friendName = friendName;
            this.friendStatus = friendStatus;
        }

        public String getFriendId() {
            return friendId;
        }

        public String getFriendName() {
            return friendName;
        }

        public String getFriendStatus() {
            return friendStatus;
        }
    }

}

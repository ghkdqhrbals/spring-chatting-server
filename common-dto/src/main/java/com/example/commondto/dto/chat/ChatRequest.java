package com.example.commondto.dto.chat;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

public class ChatRequest {
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @Setter
    public static class ChatRecordDTO {
        private String id;
        private Long roomId;
        private String sendUserId;
        private String sendUserName;
        private String message;
        private LocalDateTime createdAt;

        @Builder
        public ChatRecordDTO(String id, Long roomId, String sendUserId, String sendUserName, String message, LocalDateTime createdAt) {
            this.id = id;
            this.roomId = roomId;
            this.sendUserId = sendUserId;
            this.sendUserName = sendUserName;
            this.message = message;
            this.createdAt = createdAt;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @Setter
    public static class ChatRecordDTOsWithUser {

        private String userId;
        private String userName;
        List<ChatRecordDTO> records;

        @Builder
        public ChatRecordDTOsWithUser(String userId, String userName, List<ChatRecordDTO> records) {
            this.userId = userId;
            this.userName = userName;
            this.records = records;
        }
    }

}

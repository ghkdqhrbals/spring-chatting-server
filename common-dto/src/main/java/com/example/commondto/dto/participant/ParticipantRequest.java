package com.example.commondto.dto.participant;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class ParticipantRequest {
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @Setter
    @ToString
    public static class RemoveParticipantDto {
        private Long roomId;

        @Builder
        public RemoveParticipantDto(Long roomId) {
            this.roomId = roomId;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @Setter
    @ToString
    public static class AddParticipantRequest {
        private Long roomId;

        @Builder
        public AddParticipantRequest(Long roomId) {
            this.roomId = roomId;
        }
    }
}

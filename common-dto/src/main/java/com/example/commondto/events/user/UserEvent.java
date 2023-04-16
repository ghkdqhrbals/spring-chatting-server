package com.example.commondto.events.user;

import com.example.commondto.dto.RequestUserChangeDto;
import com.example.commondto.events.Event;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class UserEvent implements Event, Serializable {

    public UserEvent(UUID eventId, UserStatus userStatus, String userId) {
        this.eventId = eventId;
        this.userStatus = userStatus.name();
        this.userId = userId;
    }
    public UserEvent() {
    }

    private UUID eventId;
    private final Date date = new Date();
    private String userStatus;
    private String userId;

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Date getDate() {
        return date;
    }
}

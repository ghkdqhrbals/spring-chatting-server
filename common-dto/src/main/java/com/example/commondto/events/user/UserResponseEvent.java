package com.example.commondto.events.user;

import com.example.commondto.events.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UserResponseEvent implements Event {
    private UUID eventId;
    private Date date = new Date();
    private String userId;
    private String userStatus;
    private String serviceName;
    private String message;


    public UserResponseEvent() {
    }

    @Builder
    public UserResponseEvent(UUID eventId, String userId, String userStatus, String serviceName, String message) {
        this.eventId = eventId;
        this.userId = userId;
        this.userStatus = userStatus;
        this.serviceName = serviceName;
    }
    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Date getDate() {
        return date;
    }


}

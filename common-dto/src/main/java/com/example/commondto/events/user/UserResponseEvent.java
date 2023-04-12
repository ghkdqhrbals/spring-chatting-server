package com.example.commondto.events.user;

import com.example.commondto.dto.ResponseUserChangeDto;
import com.example.commondto.events.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UserResponseEvent implements Event {

    private UUID eventId;
    private Date date = new Date();

    private ResponseUserChangeDto responseUserChangeDto;
    private String userResponseStatus;

    public UserResponseEvent() {
    }

    public UserResponseEvent(UUID eventId, ResponseUserChangeDto responseUserChangeDto, String userResponseStatus, String serviceName) {
        this.eventId = eventId;
        this.responseUserChangeDto = responseUserChangeDto;
        this.userResponseStatus = userResponseStatus;
        this.serviceName = serviceName;
    }

    private String serviceName;

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Date getDate() {
        return date;
    }


}

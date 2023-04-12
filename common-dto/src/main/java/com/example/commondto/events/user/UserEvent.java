package com.example.commondto.events.user;

import com.example.commondto.dto.PaymentDto;
import com.example.commondto.dto.RequestUserChangeDto;
import com.example.commondto.dto.UserRequestDto;
import com.example.commondto.events.Event;
import com.example.commondto.events.payment.PaymentStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class UserEvent implements Event, Serializable {

    public UserEvent(UUID eventId, UserStatus userStatus, RequestUserChangeDto userDto) {
        this.eventId = eventId;
        this.userStatus = userStatus.name();
        this.userDto = userDto;
    }

    public UserEvent() {
    }

    private UUID eventId;
    private final Date date = new Date();
    private String userStatus;
    private RequestUserChangeDto userDto;

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Date getDate() {
        return date;
    }
}

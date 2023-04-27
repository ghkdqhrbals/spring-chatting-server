package com.example.commondto.events.order;

import com.example.commondto.dto.NewOrderDto;
import com.example.commondto.events.Event;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class OrderEvent implements Event {

    private final UUID eventId = UUID.randomUUID();
    private final Date date = new Date();
    private NewOrderDto newOrderDto;
    private OrderStatus orderStatus;

    public OrderEvent() {
    }

    public OrderEvent(NewOrderDto newOrderDto, OrderStatus orderStatus) {
        this.newOrderDto = newOrderDto;
        this.orderStatus = orderStatus;
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    public NewOrderDto getNewOrderDto() {
        return newOrderDto;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}

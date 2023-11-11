package com.example.commondto.events.order;

import com.example.commondto.dto.order.OrderRequest;
import com.example.commondto.events.Event;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class OrderEvent implements Event {

    private final UUID eventId = UUID.randomUUID();
    private final Date date = new Date();
    private OrderRequest.NewOrderDTO orderRequest;
    private OrderStatus orderStatus;

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    public OrderRequest.NewOrderDTO getOrderRequest() {
        return orderRequest;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}

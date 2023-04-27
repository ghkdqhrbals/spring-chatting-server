package com.example.commondto.events.customer;

import com.example.commondto.dto.CustomerDto;
import com.example.commondto.events.Event;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CustomerEvent implements Event {

    private final UUID eventId = UUID.randomUUID();
    private final Date date = new Date();
    private CustomerDto payment;
    private CustomerStatus customerStatus;

    public CustomerEvent() {
    }

    public CustomerEvent(CustomerDto payment, CustomerStatus status) {
        this.payment = payment;
        this.customerStatus = status;
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    public CustomerDto getPayment() {
        return payment;
    }

    public CustomerStatus getCustomerStatus() {
        return customerStatus;
    }
}

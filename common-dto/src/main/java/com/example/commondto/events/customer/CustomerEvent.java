package com.example.commondto.events.customer;

import com.example.commondto.dto.customer.CustomerRequest;
import com.example.commondto.events.Event;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CustomerEvent implements Event {

    private final UUID eventId = UUID.randomUUID();
    private final Date date = new Date();
    private CustomerRequest.NewCustomerDTO payment;
    private CustomerStatus customerStatus;

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    public CustomerRequest.NewCustomerDTO getPayment() {
        return payment;
    }

    public CustomerStatus getCustomerStatus() {
        return customerStatus;
    }
}

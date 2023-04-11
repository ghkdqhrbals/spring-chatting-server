package com.example.productservice.event.payment;

import com.example.productservice.dto.PaymentDto;
import com.example.productservice.event.Event;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class PaymentEvent implements Event {

    private final UUID eventId = UUID.randomUUID();
    private final Date date = new Date();
    private PaymentDto payment;
    private PaymentStatus paymentStatus;

    public PaymentEvent() {
    }

    public PaymentEvent(PaymentDto payment, PaymentStatus status) {
        this.payment = payment;
        this.paymentStatus = status;
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    public PaymentDto getPayment() {
        return payment;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
}

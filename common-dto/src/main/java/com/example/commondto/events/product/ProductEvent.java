package com.example.commondto.events.product;

import com.example.commondto.dto.ProductDto;
import com.example.commondto.events.Event;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ProductEvent implements Event {

    private final UUID eventId = UUID.randomUUID();
    private final Date date = new Date();
    private ProductDto inventory;
    private ProductStatus status;

    public ProductEvent() {
    }

    public ProductEvent(ProductDto inventory, ProductStatus status) {
        this.inventory = inventory;
        this.status = status;
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    public ProductDto getInventory() {
        return inventory;
    }

    public ProductStatus getStatus() {
        return status;
    }

}

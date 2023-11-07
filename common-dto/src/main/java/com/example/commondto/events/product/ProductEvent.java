package com.example.commondto.events.product;

import com.example.commondto.dto.product.ProductRequest;
import com.example.commondto.events.Event;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ProductEvent implements Event {

    private final UUID eventId = UUID.randomUUID();
    private final Date date = new Date();
    private ProductRequest.NewProductDTO inventory;
    private ProductStatus status;
}

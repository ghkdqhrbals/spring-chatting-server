package com.example.orderservice.entity;

import com.example.commondto.events.inventory.InventoryStatus;
import com.example.commondto.events.order.OrderStatus;
import com.example.commondto.events.payment.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.ToString;
import java.util.UUID;

@Data
@Entity
@ToString
public class PurchaseOrder {

    @Id
    private UUID id;
    private Integer userId;
    private Integer productId;
    private Integer price;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private InventoryStatus inventoryStatus;

    @Version
    private int version;

}
package com.example.orderservice.entity;

import com.example.commondto.events.inventory.InventoryStatus;
import com.example.commondto.events.order.OrderStatus;
import com.example.commondto.events.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "ORDER_TRANSACTION_TABLE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTransaction {
    // ORDER ID
    @Id
    private UUID orderId;

    private String userId;
    private Long productId;
    private Integer amount;
    private Integer price;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private InventoryStatus InventoryStatus;
}

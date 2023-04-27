package com.example.orderservice.entity;

import com.example.commondto.events.product.ProductStatus;
import com.example.commondto.events.order.OrderStatus;
import com.example.commondto.events.customer.CustomerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "ORDER_TRANSACTION_TABLE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTransaction {
    // ORDER ID
    @Id
    private UUID eventId;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;

    @Enumerated(EnumType.STRING)
    private ProductStatus ProductStatus;
}

package com.example.orderservice.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class OrderEntity implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true, name = "PRODUCT_ID")
    private String productId;

    @Column(nullable = false, name="UNIT_PRICE")
    private Integer unitPrice;

    @Column(nullable = false, name = "TOTAL_PRICE")
    private Integer totalPrice;

    @Column(nullable = false, name = "USER_ID")
    private String userId;

    @Column(nullable = false, name = "ORDER_ID", unique = true)
    private String orderId;

    @Column(nullable = false,updatable = false,insertable = false, name="CREATED_AT")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}

package com.example.productservice.data;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CATALOG_TABLE")
public class CatalogEntity implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true, name = "PRODUCT_ID")
    private String productId;

    @Column(nullable = false, name="PRODUCT_NAME")
    private String productName;

    @Column(nullable = false, name = "STOCK")
    private Integer stock;

    @Column(nullable = false, name="UNIT_PRICE")
    private Integer unitPrice;

    @Column(nullable = false,updatable = false,insertable = false, name="CREATED_AT")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}

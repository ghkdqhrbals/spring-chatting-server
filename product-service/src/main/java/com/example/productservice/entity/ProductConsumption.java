package com.example.productservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Table(name = "PRODUCT_CONSUMPTION_TABLE")
public class ProductConsumption {

    @Id
    private UUID orderId;
    private int productId;
    private int quantityConsumed;
}
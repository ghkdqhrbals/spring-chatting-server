package com.example.productservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Table(name = "PRODUCT_TABLE")
public class OrderInventory {

    @Id
    private int productId;
    private int availableInventory;

}

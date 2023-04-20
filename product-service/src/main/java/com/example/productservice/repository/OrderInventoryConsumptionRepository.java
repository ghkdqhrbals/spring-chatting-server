package com.example.productservice.repository;

import com.example.productservice.entity.ProductConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderInventoryConsumptionRepository extends JpaRepository<ProductConsumption, UUID> {
}

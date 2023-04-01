package com.example.productservice.service;

import com.example.productservice.data.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalog();
}

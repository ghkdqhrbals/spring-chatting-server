package com.example.productservice.service;

import com.example.productservice.data.CatalogEntity;
import com.example.productservice.repository.CatalogRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatalogServiceImpl implements CatalogService {
    private final CatalogRepository catalogRepository;
    private final EntityManager entityManager;

    @Autowired
    public CatalogServiceImpl(CatalogRepository catalogRepository, EntityManager entityManager) {
        this.catalogRepository = catalogRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Iterable<CatalogEntity> getAllCatalog(){
        return catalogRepository.findAll();
    }

}

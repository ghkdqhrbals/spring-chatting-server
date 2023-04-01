package com.example.productservice.controller;

import com.example.productservice.data.CatalogEntity;
import com.example.productservice.service.CatalogService;
import com.example.productservice.vo.ResponseCatalog;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/catalog-service")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getAllCatalog(){
        Iterable<CatalogEntity> allCatalog = catalogService.getAllCatalog();

        List<ResponseCatalog> results = new ArrayList<>();

        allCatalog.forEach(v ->{
            results.add(new ModelMapper().map(v, ResponseCatalog.class));

        });

        return ResponseEntity.ok(results);
    }
}

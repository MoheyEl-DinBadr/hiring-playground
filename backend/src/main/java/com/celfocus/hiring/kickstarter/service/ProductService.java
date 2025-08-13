package com.celfocus.hiring.kickstarter.service;

import com.celfocus.hiring.kickstarter.db.entity.ProductEntity;
import com.celfocus.hiring.kickstarter.db.repo.ProductRepository;
import com.celfocus.hiring.kickstarter.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Optional<Product> getProduct(String sku) {
        return productRepository.findBySku(sku)
                .map(this::mapProductEntityToProduct);
    }

    private Product mapProductEntityToProduct(ProductEntity productEntity) {
        return productEntity;
    }


}

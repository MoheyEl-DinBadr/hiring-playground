package com.celfocus.hiring.kickstarter.db.repo;

import com.celfocus.hiring.kickstarter.db.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryTest {


    private final ProductRepository productRepository;

    public ProductRepositoryTest(@Autowired ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Test
    void testFindProductBySku() {
        // When
        Optional<ProductEntity> product = productRepository.findBySku("SKUTEST2");
        // Then
        assertTrue(product.isPresent());
        assertEquals("Slim-fitting style, contrast raglan long sleeve, three-button henley placket, light weight & soft fabric for breathable and comfortable wearing. And Solid stitched shirts with round neck made for durability and a great fit for casual fashion wear and diehard baseball fans. The Henley style round neckline includes a three-button placket.", product.get().getDescription());
    }
}

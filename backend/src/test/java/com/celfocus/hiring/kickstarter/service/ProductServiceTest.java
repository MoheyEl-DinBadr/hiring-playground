package com.celfocus.hiring.kickstarter.service;

import com.celfocus.hiring.kickstarter.db.entity.ProductEntity;
import com.celfocus.hiring.kickstarter.db.repo.ProductRepository;
import com.celfocus.hiring.kickstarter.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String TEST_PRODUCT_SKU = "product1";
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;
    private ProductEntity testProductEntity;

    @BeforeEach
    void setUp() {
        testProductEntity = new ProductEntity();
        testProductEntity.setSku(TEST_PRODUCT_SKU);
        testProductEntity.setName("Test Product");
        testProductEntity.setPrice(BigDecimal.TEN);
        testProductEntity.setDescription("Test Description");
    }

    @Test
    void getProduct_Success() {
        // Arrange
        when(productRepository.findBySku(TEST_PRODUCT_SKU)).thenReturn(Optional.of(testProductEntity));

        // Act
        Optional<Product> result = productService.getProduct(TEST_PRODUCT_SKU);

        // Assert
        assertTrue(result.isPresent());
        Product product = result.get();
        assertEquals(TEST_PRODUCT_SKU, product.getSku());
        assertEquals(testProductEntity.getName(), product.getName());
        assertEquals(testProductEntity.getPrice(), product.getPrice());
        assertEquals(testProductEntity.getDescription(), product.getDescription());
        verify(productRepository).findBySku(TEST_PRODUCT_SKU);
    }

    @Test
    void getProduct_NonExistent_ReturnsEmpty() {
        // Arrange
        when(productRepository.findBySku(TEST_PRODUCT_SKU)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProduct(TEST_PRODUCT_SKU);

        // Assert
        assertTrue(result.isEmpty());
        verify(productRepository).findBySku(TEST_PRODUCT_SKU);
    }
}

package com.celfocus.hiring.kickstarter.service;

import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import com.celfocus.hiring.kickstarter.db.repo.CartItemRepository;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import com.celfocus.hiring.kickstarter.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    private static final String TEST_ITEM_ID = "item1";
    private static final Long TEST_CART_ID = 1L;
    @Mock
    private ProductService productService;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private CartItemService cartItemService;
    private CartEntity testCart;
    private Product testProduct;
    private CartItemEntity testCartItem;

    @BeforeEach
    void setUp() {
        testCart = new CartEntity();
        testCart.setId(TEST_CART_ID);

        testProduct = new Product();
        testProduct.setSku(TEST_ITEM_ID);
        testProduct.setPrice(BigDecimal.TEN);

        testCartItem = new CartItemEntity();
        testCartItem.setCartId(TEST_CART_ID);
        testCartItem.setItemId(TEST_ITEM_ID);
        testCartItem.setQuantity(1);
        testCartItem.setPrice(BigDecimal.TEN);
    }

    @Test
    void getForUpdate_Success() {
        // Arrange
        when(cartItemRepository.findByIdForUpdate(TEST_CART_ID, TEST_ITEM_ID))
                .thenReturn(Optional.of(testCartItem));

        // Act
        Optional<CartItem> result = cartItemService.getForUpdate(TEST_CART_ID, TEST_ITEM_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_ITEM_ID, result.get().getItemId());
        assertEquals(TEST_CART_ID, result.get().getCartId());
        verify(cartItemRepository).findByIdForUpdate(TEST_CART_ID, TEST_ITEM_ID);
    }

    @Test
    void getForUpdate_NonExistentItem_ReturnsEmpty() {
        // Arrange
        when(cartItemRepository.findByIdForUpdate(TEST_CART_ID, TEST_ITEM_ID))
                .thenReturn(Optional.empty());

        // Act
        Optional<CartItem> result = cartItemService.getForUpdate(TEST_CART_ID, TEST_ITEM_ID);

        // Assert
        assertTrue(result.isEmpty());
        verify(cartItemRepository).findByIdForUpdate(TEST_CART_ID, TEST_ITEM_ID);
    }

    @Test
    void updateQuantityById_Success() {
        // Arrange
        Integer increment = 1;

        // Act
        cartItemService.updateQuantityById(TEST_CART_ID, TEST_ITEM_ID, increment);

        // Assert
        verify(cartItemRepository).updateQuantityById(TEST_CART_ID, TEST_ITEM_ID, increment);
    }

    @Test
    void addNewItemToCart_Success() {
        // Arrange
        when(productService.getProduct(TEST_ITEM_ID)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.save(any(CartItemEntity.class))).thenReturn(testCartItem);

        // Act
        cartItemService.addNewItemToCart(TEST_ITEM_ID, testCart);

        // Assert
        verify(productService).getProduct(TEST_ITEM_ID);
        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void addNewItemToCart_ProductNotFound_ThrowsException() {
        // Arrange
        when(productService.getProduct(TEST_ITEM_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartItemService.addNewItemToCart(TEST_ITEM_ID, testCart));
        verify(productService).getProduct(TEST_ITEM_ID);
        verify(cartItemRepository, never()).save(any(CartItemEntity.class));
    }

    @Test
    void deleteById_Success() {
        // Arrange
        CartItemPK id = new CartItemPK(TEST_ITEM_ID, TEST_CART_ID);

        // Act
        cartItemService.deleteById(id);

        // Assert
        verify(cartItemRepository).deleteById(id);
    }
}

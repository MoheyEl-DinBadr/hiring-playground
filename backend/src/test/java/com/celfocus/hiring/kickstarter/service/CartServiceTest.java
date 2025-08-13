package com.celfocus.hiring.kickstarter.service;

import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import com.celfocus.hiring.kickstarter.db.repo.CartRepository;
import com.celfocus.hiring.kickstarter.domain.Cart;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_ITEM_ID = "item1";
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemService cartItemService;
    @InjectMocks
    private CartService cartService;
    private CartEntity testCart;
    private CartItemInput testItemInput;

    @BeforeEach
    void setUp() {
        testCart = new CartEntity();
        testCart.setId(1L);
        testCart.setUserId(TEST_USERNAME);
        testCart.setItems(new ArrayList<>());

        testItemInput = new CartItemInput(TEST_ITEM_ID);
    }

    @Test
    void addItemToCart_NewCart_Success() {
        // Arrange
        when(cartRepository.findByUserIdForUpdate(TEST_USERNAME)).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartEntity.class))).thenReturn(testCart);

        // Act
        cartService.addItemToCart(TEST_USERNAME, testItemInput);

        // Assert
        verify(cartRepository).findByUserIdForUpdate(TEST_USERNAME);
        verify(cartRepository).save(any(CartEntity.class));
        verify(cartItemService).getForUpdate(testCart.getId(), TEST_ITEM_ID);
    }

    @Test
    void addItemToCart_ExistingCart_Success() {
        // Arrange
        when(cartRepository.findByUserIdForUpdate(TEST_USERNAME)).thenReturn(Optional.of(testCart));
        when(cartItemService.getForUpdate(testCart.getId(), TEST_ITEM_ID))
                .thenReturn(Optional.empty());

        // Act
        cartService.addItemToCart(TEST_USERNAME, testItemInput);

        // Assert
        verify(cartRepository).findByUserIdForUpdate(TEST_USERNAME);
        verify(cartItemService).addNewItemToCart(TEST_ITEM_ID, testCart);
    }

    @Test
    void addItemToCart_ExistingCartAndItem_Success() {
        // Arrange
        CartItemEntity existingItem = new CartItemEntity();
        existingItem.setCartId(testCart.getId());
        existingItem.setItemId(TEST_ITEM_ID);

        when(cartRepository.findByUserIdForUpdate(TEST_USERNAME)).thenReturn(Optional.of(testCart));
        when(cartItemService.getForUpdate(testCart.getId(), TEST_ITEM_ID))
                .thenReturn(Optional.of(existingItem));

        // Act
        cartService.addItemToCart(TEST_USERNAME, testItemInput);

        // Assert
        verify(cartRepository).findByUserIdForUpdate(TEST_USERNAME);
        verify(cartItemService).updateQuantityById(testCart.getId(), TEST_ITEM_ID, 1);
    }

    @Test
    void clearCart_Success() {
        // Act
        cartService.clearCart(TEST_USERNAME);

        // Assert
        verify(cartRepository).deleteByUserId(TEST_USERNAME);
    }

    @Test
    void getCart_Success() {
        // Arrange
        when(cartRepository.findByUserIdForUpdate(TEST_USERNAME)).thenReturn(Optional.of(testCart));

        // Act
        Cart<? extends CartItem> result = cartService.getCart(TEST_USERNAME);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUserId());
        verify(cartRepository).findByUserIdForUpdate(TEST_USERNAME);
    }

    @Test
    void getCart_NonExistentCart_ThrowsException() {
        // Arrange
        when(cartRepository.findByUserIdForUpdate(TEST_USERNAME)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.getCart(TEST_USERNAME));
        verify(cartRepository).findByUserIdForUpdate(TEST_USERNAME);
    }

    @Test
    void removeItemFromCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(TEST_USERNAME)).thenReturn(Optional.of(testCart));

        // Act
        cartService.removeItemFromCart(TEST_USERNAME, TEST_ITEM_ID);

        // Assert
        verify(cartRepository).findByUserId(TEST_USERNAME);
        verify(cartItemService).deleteById(any(CartItemPK.class));
    }

    @Test
    void removeItemFromCart_NonExistentCart_NoAction() {
        // Arrange
        when(cartRepository.findByUserId(TEST_USERNAME)).thenReturn(Optional.empty());

        // Act
        cartService.removeItemFromCart(TEST_USERNAME, TEST_ITEM_ID);

        // Assert
        verify(cartRepository).findByUserId(TEST_USERNAME);
        verify(cartItemService, never()).deleteById(any(CartItemPK.class));
    }
}

package com.celfocus.hiring.kickstarter.service;

import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import com.celfocus.hiring.kickstarter.db.repo.CartItemRepository;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class CartItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartItemService.class);
    private final ProductService productService;
    private final CartItemRepository cartItemRepository;

    public CartItemService(ProductService productService, CartItemRepository cartItemRepository) {
        this.productService = productService;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<CartItem> getForUpdate(Long cartId, String itemId) {
        LOGGER.info("Cart item action - type=GET_FOR_UPDATE, cartId={}, itemId={}", cartId, itemId);
        return cartItemRepository.findByIdForUpdate(cartId, itemId)
                .map(this::mapToCartItem);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateQuantityById(Long cartId, String itemId, Integer increment) {
        cartItemRepository.updateQuantityById(cartId, itemId, increment);
    }


    @Transactional
    public void addNewItemToCart(String itemId, CartEntity cart) {
        LOGGER.info("Cart item action - type=ADD_NEW_ITEM, cartId={}, itemId={}, userId={}", 
            cart.getId(), itemId, cart.getUserId());
        var product = productService.getProduct(itemId)
                .orElseThrow(() -> new RuntimeException("Cart Item not found"));
        var cartItem = new CartItemEntity();
        cartItem.setQuantity(1);
        cartItem.setItemId(itemId);
        cartItem.setCartId(cart.getId());
        cartItem.setCart(cart);
        cartItem.setPrice(product.getPrice());
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void deleteById(CartItemPK id) {
        LOGGER.info("Cart item action - type=DELETE_ITEM, cartId={}, itemId={}", id.getCartId(), id.getItemId());
        cartItemRepository.deleteById(id);
    }

    private CartItem mapToCartItem(CartItemEntity cartItemEntity) {
        return cartItemEntity;
    }
}

package com.celfocus.hiring.kickstarter.service;

import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import com.celfocus.hiring.kickstarter.db.repo.CartRepository;
import com.celfocus.hiring.kickstarter.domain.Cart;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class CartService {
    private final CartRepository cartRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CartService.class);
    private final CartItemService cartItemService;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemService cartItemService) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
    }

    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 200)
    )
    @Transactional
    public void addItemToCart(String username, CartItemInput itemInput) {
        LOGGER.info("Adding item to cart: {}", itemInput.itemId());
        var cart = cartRepository.findByUserIdForUpdate(username).orElseGet(() -> {
            LOGGER.info("Creating new cart for user: {}", username);
            var newCart = new CartEntity();
            newCart.setUserId(username);
            return cartRepository.save(newCart);
        });
        cartItemService.getForUpdate(cart.getId(), itemInput.itemId())
                .ifPresentOrElse((item) -> {
                    cartItemService.updateQuantityById(item.getCartId(), item.getItemId(), 1);
                    LOGGER.info("Updated quantity of item in cart: {}", itemInput.itemId());
                }, () -> {
                    cartItemService.addNewItemToCart(itemInput.itemId(), cart);
                    LOGGER.info("Added new item to cart: {}", itemInput.itemId());
                });
        LOGGER.info("Finished adding item to cart: {}", itemInput.itemId());
    }

    public void clearCart(String username) {
        cartRepository.deleteByUserId(username);
    }


    @Transactional
    public Cart<? extends CartItem> getCart(String username) {
        return cartRepository.findByUserIdForUpdate(username)
                .map(this::mapToCart)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Cart was found for the user"));
    }

    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 200)
    )
    public void removeItemFromCart(String username, String itemId) {
        cartRepository.findByUserId(username)
                .ifPresent(cart -> cartItemService.deleteById(new CartItemPK(itemId, cart.getId())));
    }

    private Cart<? extends CartItem> mapToCart(CartEntity cartEntity) {
        Cart<CartItemEntity> cart = new Cart<>();
        cart.setUserId(cartEntity.getUserId());
        cart.setItems(cartEntity.getItems());
        return cart;
    }
}

package com.celfocus.hiring.kickstarter.api;

import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.api.dto.CartItemResponse;
import com.celfocus.hiring.kickstarter.api.dto.CartResponse;
import com.celfocus.hiring.kickstarter.domain.Cart;
import com.celfocus.hiring.kickstarter.domain.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CartAPIController.CARTS_PATH)
public class CartAPIController implements CartAPI {

    static final String CARTS_PATH = "/api/v1/carts";

    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartAPIController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from Celfocus!";
    }

    @Override
    @PreAuthorize("hasRole('USER') and (#username == authentication.name)")
    public ResponseEntity<Void> addItemToCart(String username, CartItemInput itemInput) {
        cartService.addItemToCart(username, itemInput);
        return ResponseEntity.status(201).build();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER') and (#username == authentication.name)")
    public ResponseEntity<Void> clearCart(String username) {
        cartService.clearCart(username);
        return ResponseEntity.status(204).build();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER') and (#username == authentication.name)")
    public ResponseEntity<CartResponse> getCart(String username) {
        var cart = cartService.getCart(username);
        return ResponseEntity.ok(mapToCartResponse(cart));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER') and (#username == authentication.name)")
    public ResponseEntity<Void> removeItemFromCart(String username, String itemId) {
        cartService.removeItemFromCart(username, itemId);
        return ResponseEntity.status(204).build();
    }

    private CartResponse mapToCartResponse(Cart<? extends CartItem> cart) {
        return new CartResponse(cart.getItems().stream().map(this::mapToCartItemResponse).toList());
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        var product = productService.getProduct(item.getItemId());
        return new CartItemResponse(item.getItemId(), item.getQuantity(), product.orElseThrow().getPrice(), product.orElseThrow().getName());
    }
}

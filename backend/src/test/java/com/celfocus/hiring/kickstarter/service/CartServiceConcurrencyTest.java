package com.celfocus.hiring.kickstarter.service;

import com.celfocus.hiring.kickstarter.api.dto.CartItemInput;
import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import com.celfocus.hiring.kickstarter.db.repo.CartItemRepository;
import com.celfocus.hiring.kickstarter.db.repo.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.annotation.EnableRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableRetry
@SpringBootTest
class CartServiceConcurrencyTest {

    private static final String USERNAME = "testUser";
    private static final String ITEM_ID = "SKUTEST1";
    private static final int CONCURRENT_USERS = 5;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void addItemToCart_ConcurrentUpdates_HandledCorrectly() throws InterruptedException {
        // Arrange
        CartItemInput input = new CartItemInput(ITEM_ID);
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(CONCURRENT_USERS);

        // Act - simulate concurrent users adding same item
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    cartService.addItemToCart(USERNAME, input);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Start all threads
        boolean completed = endLatch.await(20, TimeUnit.SECONDS);
        executorService.shutdown();

        // Assert
        assertTrue(completed, "Concurrent operations did not complete in time");

        // Verify final cart state
        CartEntity cart = cartRepository.findByUserId(USERNAME).orElseThrow();

        CartItemEntity firstItem = cartItemRepository.findById(new CartItemPK(ITEM_ID, cart.getId())).orElseThrow();
        assertEquals(CONCURRENT_USERS, firstItem.getQuantity(),
                "Final quantity should match number of concurrent additions");
    }


    @Test
    void removeItemFromCart_ConcurrentUpdates_HandledCorrectly() throws InterruptedException {
        // First add items to cart
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            cartService.addItemToCart(USERNAME, new CartItemInput(ITEM_ID));
        }

        // Arrange for concurrent removal
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(CONCURRENT_USERS);

        // Act - simulate concurrent users removing same item
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    cartService.removeItemFromCart(USERNAME, ITEM_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // Assert
        assertTrue(completed, "Concurrent operations did not complete in time");
        AtomicBoolean isEmpty = new AtomicBoolean(false);
        var cartOptional = cartRepository.findByUserId(USERNAME);
        cartOptional.ifPresentOrElse(cart -> {
            var cartItem = cartItemRepository.findById(new CartItemPK(ITEM_ID, cart.getId()));
            isEmpty.set(cartItem.isEmpty());
        }, () -> {
            isEmpty.set(true);
        });

        assertTrue(isEmpty.get(),
                "Cart should be empty after all items are removed");
    }
}

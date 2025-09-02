package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.CartDto;
import com.whalewearables.backend.dto.CartItemsDto;
import com.whalewearables.backend.model.Cart;
import com.whalewearables.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    // 1️⃣ Get Cart
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    // 2️⃣ Add Item
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDto> addItem(@PathVariable Long userId,@RequestParam Long productId,@RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addItem(userId, productId, quantity));
    }

    // 3️⃣ Update Quantity
    @PutMapping("/{cartId}/update")
    public ResponseEntity<CartDto> updateQuantity(@PathVariable Long cartId,
                                               @RequestParam Long productId,
                                               @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(cartId, productId, quantity));
    }

    // 4️⃣ Remove Item
    @DeleteMapping("/{cartId}/remove")
    public ResponseEntity<CartDto> removeItem(@PathVariable Long cartId,
                                           @RequestParam Long productId) {
        return ResponseEntity.ok(cartService.removeItem(cartId, productId));
    }

    // 5️⃣ Clear Cart
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<CartDto> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/merge")
    public ResponseEntity<CartDto> mergeGuestCart(
            @RequestBody List<CartItemsDto> guestItems,
            @RequestParam Long userId) {

        CartDto userCart = cartService.mergeGuestCart(userId, guestItems);
        return ResponseEntity.ok(userCart);
    }

}

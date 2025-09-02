package com.whalewearables.backend.service;

import com.whalewearables.backend.dto.CartDto;
import com.whalewearables.backend.dto.CartItemsDto;
import com.whalewearables.backend.model.Cart;

import java.util.List;

public interface CartService {

    public CartDto getCart(Long userId);
    public CartDto addItem(Long userId,Long productId, int quantity);
    public CartDto updateQuantity(Long cartId, Long productId, int quantity);
    public CartDto removeItem(Long cartId, Long productId);
    public CartDto clearCart(Long cartId);
    CartDto  mergeGuestCart(Long userId, List<CartItemsDto> guestItems);
}

package com.whalewearables.backend.dto;

import java.util.List;

public class CartDto {

    private Long cartId;
    private Long userId;
    private List<CartItemsDto> items;

    public CartDto() {}

    public CartDto(Long cartId, Long userId, List<CartItemsDto> items) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CartItemsDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemsDto> items) {
        this.items = items;
    }
}

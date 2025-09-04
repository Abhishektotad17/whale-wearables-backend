package com.whalewearables.backend.service.impl;

import com.whalewearables.backend.dto.CartDto;
import com.whalewearables.backend.dto.CartItemsDto;
import com.whalewearables.backend.model.Cart;
import com.whalewearables.backend.model.CartItems;
import com.whalewearables.backend.model.Product;
import com.whalewearables.backend.model.User;
import com.whalewearables.backend.repository.CartItemRepository;
import com.whalewearables.backend.repository.CartRepository;
import com.whalewearables.backend.repository.ProductRepository;
import com.whalewearables.backend.repository.UserRepository;
import com.whalewearables.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepo;
    @Autowired
    private CartItemRepository cartItemRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ProductRepository productRepo;

    private CartDto mapToDto(Cart cart) {
        List<CartItemsDto> itemsDto = cart.getItems().stream()
                .map(item -> {
                    Product product = productRepo.findById(item.getProductId()).orElse(null);
                    return new CartItemsDto(
                            item.getCartItemId(),
                            item.getProductId(),
                            product != null ? product.getName() : null,
                            product != null ? product.getPrice() : null,
                            product != null ? product.getImageUrl() : null,
                            item.getQuantity()
                    );
                })
                .toList();

        return new CartDto(cart.getCartId(), cart.getUser().getId(), itemsDto);
    }


    // helper method to fetch Cart entity by cartId
    private Cart getCartEntity(Long cartId) {
        return cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Override
    public CartDto getCart(Long userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    // create new cart for user if not found
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });

        return mapToDto(cart);
    }

    @Override
    public CartDto addItem(Long userId, Long productId, int quantity) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItems> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItems newItem = new CartItems();
            newItem.setCart(cart);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        cart = cartRepo.save(cart);
        return mapToDto(cart);
    }


    @Override
    public CartDto updateQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = getCartEntity(cartId);

        cart.getItems().removeIf(it -> {
            if (it.getProductId().equals(productId)) {
                if (quantity <= 0) {
                    return true; // remove item
                } else {
                    it.setQuantity(quantity);
                }
            }
            return false;
        });

        cart = cartRepo.save(cart);
        return mapToDto(cart);
    }

    @Override
    public CartDto removeItem(Long cartId, Long productId) {
        Cart cart = getCartEntity(cartId);
        cart.getItems().removeIf(it -> it.getProductId().equals(productId));
        cart = cartRepo.save(cart);
        return mapToDto(cart);

    }

    @Override
    public CartDto clearCart(Long cartId) {
        Cart cart = getCartEntity(cartId);
        cart.getItems().clear();
        cart = cartRepo.save(cart);
        return mapToDto(cart);
    }

    public CartDto  mergeGuestCart(Long userId, List<CartItemsDto> guestItems) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepo.findById(userId).orElseThrow());
                    return cartRepo.save(newCart);
                });

        for (CartItemsDto dto : guestItems) {
            Optional<CartItems> existing = cart.getItems().stream()
                    .filter(ci -> ci.getProductId().equals(dto.getProductId()))
                    .findFirst();

            if (existing.isPresent()) {
                // If item already exists, update quantity
                CartItems item = existing.get();
                item.setQuantity(item.getQuantity() + dto.getQuantity());
            } else {
                // Create new cart item
                CartItems newItem = new CartItems();
                newItem.setProductId(dto.getProductId()); // since it's just Long
                newItem.setQuantity(dto.getQuantity());
                newItem.setCart(cart);
                cart.getItems().add(newItem);
            }
        }
        cart = cartRepo.save(cart);
        return mapToDto(cart);
    }
}

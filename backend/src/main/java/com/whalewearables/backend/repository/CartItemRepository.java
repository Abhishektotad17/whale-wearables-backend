package com.whalewearables.backend.repository;

import com.whalewearables.backend.model.Cart;
import com.whalewearables.backend.model.CartItems;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems, Long> {

    List<CartItems> findByCart_CartId(Long cartId);
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItems ci WHERE ci.cart = :cart")
    void deleteByCart(@Param("cart") Cart cart);
}
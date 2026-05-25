package com.whalewearables.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")  // both SELLER and ADMIN can access
public class SellerController {


    // Placeholder — add product listing, inventory, orders etc. later
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        return ResponseEntity.ok(Map.of("message", "Seller dashboard — coming soon"));
    }

    @GetMapping("/products")
    public ResponseEntity<?> myProducts() {
        return ResponseEntity.ok(Map.of("message", "Seller products — coming soon"));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> myOrders() {
        return ResponseEntity.ok(Map.of("message", "Seller orders — coming soon"));
    }
}

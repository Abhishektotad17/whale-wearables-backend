package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.UserDto;
import com.whalewearables.backend.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // GET /api/users/profile — any logged-in user can get their own profile
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        String email = authentication.getName();

        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(
                        Map.of("user", UserDto.fromEntity(user))
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users/{email}/orders — user can only see their own, admin sees all
    @GetMapping("/{email}/orders")
    @PreAuthorize("authentication.name == #email or hasRole('ADMIN')")
    public ResponseEntity<?> getUserOrders(@PathVariable String email) {
        // Placeholder — wire up order service later
        return ResponseEntity.ok(Map.of("message", "Orders for " + email + " — coming soon"));
    }

    // Placeholder — add address management, preferences etc. later
    @GetMapping("/addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAddresses() {
        return ResponseEntity.ok(Map.of("message", "Addresses — coming soon"));
    }
}

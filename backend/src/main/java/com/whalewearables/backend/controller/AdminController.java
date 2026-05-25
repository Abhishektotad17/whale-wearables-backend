package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.UserDto;
import com.whalewearables.backend.model.Role;
import com.whalewearables.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Only admins can access any endpoint in this controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GET /api/admin/users — list all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(
                adminService.getAllUsers()
                        .stream()
                        .map(UserDto::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    // POST /api/admin/users/{id}/roles — assign a role
    // Body: { "role": "SELLER" }
    @PostMapping("/users/{id}/roles")
    public ResponseEntity<?> assignRole(@PathVariable Long id, @RequestBody Map<String, String> body)
    {
        Role role = Role.valueOf(body.get("role").toUpperCase());
        adminService.assignRole(id, role);
        return ResponseEntity.ok(Map.of("message", "Role " + role + " assigned to user " + id));
    }

    // DELETE /api/admin/users/{id}/roles — revoke a role
    // Body: { "role": "SELLER" }
    @DeleteMapping("/users/{id}/roles")
    public ResponseEntity<?> revokeRole(@PathVariable Long id,
                                        @RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role").toUpperCase());
        adminService.revokeRole(id, role);
        return ResponseEntity.ok(Map.of("message", "Role " + role + " revoked from user " + id));
    }

    // Placeholder — add product management, order management etc. later
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        return ResponseEntity.ok(Map.of("message", "Admin dashboard — coming soon"));
    }
}

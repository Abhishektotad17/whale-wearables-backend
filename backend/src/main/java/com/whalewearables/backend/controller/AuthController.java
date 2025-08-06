package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.AuthRequest;
import com.whalewearables.backend.dto.GoogleLoginRequest;
import com.whalewearables.backend.dto.RegisterRequest;
import com.whalewearables.backend.model.User;
import com.whalewearables.backend.security.JwtUtil;
import com.whalewearables.backend.service.impl.UserServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userServiceImpl;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authMgr;
    private final PasswordEncoder passwordEncoder;

    private final String GOOGLE_CLIENT_ID = "829277785533-b6moncq6ikqke5okefkve9uj0734n51g.apps.googleusercontent.com";

    public AuthController(UserServiceImpl userSvcImpl, JwtUtil jwtUtil,
                          AuthenticationManager authMgr, PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userSvcImpl;
        this.jwtUtil = jwtUtil;
        this.authMgr = authMgr;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userServiceImpl.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User u = userServiceImpl.registerLocalUser(req.getName(), req.getEmail(), req.getPassword());
        return ResponseEntity.ok(Map.of("user", u));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req, HttpServletResponse resp) {
        try {
            authMgr.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        User u = userServiceImpl.findByEmail(req.getEmail()).get();
        String token = jwtUtil.generateToken(u.getEmail());
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        resp.addCookie(cookie);
        return ResponseEntity.ok(Map.of("user", u));
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest req, HttpServletResponse resp) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();

            GoogleIdToken idToken = verifier.verify(req.getToken());
            if (idToken == null) throw new Exception("Invalid token");

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture"); // Extract picture

            User u = userServiceImpl.findByEmail(email)
                    .orElseGet(() -> userServiceImpl.registerGoogleUser(name, email,picture));

            // Update picture if it changed (optional)
            if (u.getPicture() == null || !u.getPicture().equals(picture)) {
                u.setPicture(picture);
                userServiceImpl.save(u); // Add a `save` method in service if not exists
            }

            String token = jwtUtil.generateToken(u.getEmail());
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            resp.addCookie(cookie);
            return ResponseEntity.ok(Map.of("user", u));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google login failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse resp) {
        Cookie c = new Cookie("jwt", null);
        c.setHttpOnly(true);
        c.setMaxAge(0);
        c.setPath("/");
        resp.addCookie(c);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest req) {
        String token = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("jwt".equals(c.getName())) {
                    token = c.getValue();
                }
            }
        }
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        String email = jwtUtil.extractUsername(token);
        User u = userServiceImpl.findByEmail(email).orElse(null);
        return ResponseEntity.ok(Map.of("user", u));
    }
}

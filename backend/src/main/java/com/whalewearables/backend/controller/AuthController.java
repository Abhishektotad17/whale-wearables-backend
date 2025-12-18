package com.whalewearables.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.whalewearables.backend.dto.AuthRequest;
import com.whalewearables.backend.dto.GoogleLoginRequest;
import com.whalewearables.backend.dto.RegisterRequest;
import com.whalewearables.backend.dto.UserDto;
import com.whalewearables.backend.model.User;
import com.whalewearables.backend.security.JwtUtil;
import com.whalewearables.backend.service.impl.UserServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
    @Value("${google.client.secret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${google.redirect.uri}")
    private String GOOGLE_REDIRECT_URI;

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

        String identifier = req.getIdentifier();
        String password = req.getPassword();

        if (identifier == null || password == null) {
            return ResponseEntity.badRequest().body("Missing credentials");
        }

        Optional<User> optionalUser = userServiceImpl.findByUsernameOrEmail(identifier);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = optionalUser.get();
        try {
            authMgr.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), req.getPassword())
            );
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setSecure(false); // ❗Set to true only if using HTTPS
        resp.addCookie(cookie);
        return ResponseEntity.ok(Map.of("user", user));
    }

//    @PostMapping("/google")
//    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest req, HttpServletResponse resp) {
//        try {
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                    GoogleNetHttpTransport.newTrustedTransport(),
//                    JacksonFactory.getDefaultInstance()
//            ).setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();
//
//            GoogleIdToken idToken = verifier.verify(req.getToken());
//            if (idToken == null) throw new Exception("Invalid token");
//
//            GoogleIdToken.Payload payload = idToken.getPayload();
//            String email = payload.getEmail();
//            String name = (String) payload.get("name");
//            String picture = (String) payload.get("picture"); // Extract picture
//
//            User u = userServiceImpl.findByEmail(email)
//                    .orElseGet(() -> userServiceImpl.registerGoogleUser(name, email,picture));
//
//            // Update picture if it changed (optional)
//            if (u.getPicture() == null || !u.getPicture().equals(picture)) {
//                u.setPicture(picture);
//                userServiceImpl.save(u); // Add a `save` method in service if not exists
//            }
//
//            String token = jwtUtil.generateToken(u.getEmail());
//            Cookie cookie = new Cookie("jwt", token);
//            cookie.setHttpOnly(true);
//            cookie.setPath("/");
//            cookie.setMaxAge(24 * 60 * 60);
//            resp.addCookie(cookie);
//            return ResponseEntity.ok(Map.of("user", u));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google login failed");
//        }
//    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body, HttpServletResponse resp) {
        try {
            String code = body.get("code");
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body("Missing authorization code");
            }

            // Step 1 — Exchange auth code for tokens
            HttpClient client = HttpClient.newHttpClient();
            String requestBody =
                    "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                            "&client_id=" + GOOGLE_CLIENT_ID +
                            "&client_secret=" + GOOGLE_CLIENT_SECRET +
                            "&redirect_uri=" + URLEncoder.encode(GOOGLE_REDIRECT_URI, StandardCharsets.UTF_8) + // must match Google Console
                            "&grant_type=authorization_code";

            HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

            if (tokenResponse.statusCode() != 200) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Failed to exchange code for tokens: " + tokenResponse.body());
            }

            Map<String, Object> tokenJson = new ObjectMapper().readValue(tokenResponse.body(), Map.class);
            String idTokenString = (String) tokenJson.get("id_token");
            if (idTokenString == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No ID token returned from Google");
            }

            // Step 2 — Verify ID token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) throw new Exception("Invalid ID token");

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            // Step 3 — Fetch or create user
            User u = userServiceImpl.findByEmail(email)
                    .orElseGet(() -> userServiceImpl.registerGoogleUser(name, email, picture));

            // Update picture if changed
            if (u.getPicture() == null || !u.getPicture().equals(picture)) {
                u.setPicture(picture);
                userServiceImpl.save(u);
            }

            // Step 4 — Create JWT and set cookie
            String jwtToken = jwtUtil.generateToken(u.getEmail());
            Cookie cookie = new Cookie("jwt", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            resp.addCookie(cookie);

            return ResponseEntity.ok(Map.of("user", UserDto.fromEntity(u)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Google login failed: " + e.getMessage());
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
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        return ResponseEntity.ok(Map.of("user", UserDto.fromEntity(u)));
    }
}

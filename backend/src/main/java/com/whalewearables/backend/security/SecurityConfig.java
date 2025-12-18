package com.whalewearables.backend.security;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) { this.jwtFilter = jwtFilter; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://d30rl42wk76bbv.cloudfront.net","https://d30rl42wk76bbv.cloudfront.net"));
                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            config.setAllowedHeaders(List.of("*"));
                            config.setAllowCredentials(true); // Cookies allowed
                            config.setExposedHeaders(List.of("Authorization", "Content-Type"));
                            config.setMaxAge(3600L); // Cache preflight response for 1 hour
                            return config;
                        })
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                                // Public endpoints (no auth required)
                                .requestMatchers(
                                        "/api/auth/**",        // login, signup, google
                                        "/api/home",
                                        "/api/products/**",    // product listing, details
                                        "/api/contact",         // contact form
                                        "/api/chat/**"
                                ).permitAll()

                                // Protected endpoints (auth required)
                                .requestMatchers(
                                        "/api/cart/**",        // cart operations
                                        "/api/orders/**",      // order creation, payment status
                                        "/api/users/**"       // user profile, addresses etc
                                ).authenticated()

                                // Any other endpoints → require authentication
                                .anyRequest().authenticated()
//                        .requestMatchers("/api/auth/**", "/api/home",
//                                "/api/orders",
//                                "/api/orders/*/token",
//                                "/api/orders/*/status",
//                                "/api/orders/*",
//                                "/api/products",
//                                "/api/products/*",
//                                "/api/cart",
//                                "/api/products/add-with-image",
//                                "/api/contact").permitAll()
//                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

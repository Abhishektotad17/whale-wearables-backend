package com.whalewearables.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, CustomUserDetailsService userDetailsService)
    {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
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
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())  // 401
                        .accessDeniedHandler(accessDeniedHandler())             // 403
                )
                .authorizeHttpRequests(auth -> auth
                                // Public endpoints (no auth required)
                                .requestMatchers(
                                        "/api/auth/**",        // login, signup, google
                                        "/api/home",
                                        "/api/products/**",    // product listing, details
                                        "/api/contact",         // contact form
                                        "/api/gemini/**",
                                        "/images/**"
                                ).permitAll()

                                // Admin only
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                //Seller + Admin
                                .requestMatchers(("/api/seller/**")).hasAnyRole("SELLER","ADMIN")

                                // Protected endpoints (auth required)
                                .requestMatchers(
                                        "/api/cart/**",        // cart operations
                                        "/api/orders/**",      // order creation, payment status
                                        "/api/users/**"       // user profile, addresses etc
                                ).authenticated()

                                // Any other endpoints → require authentication
                                .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 401 Unauthorized — returned when no valid JWT is present.
     * Returns JSON instead of the default redirect to /login.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getWriter(),
                    Map.of("error", "Unauthorized", "message", authException.getMessage()));
        };
    }

    /**
     * 403 Forbidden — returned when a valid JWT exists but lacks the required role.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getWriter(),
                    Map.of("error", "Forbidden",
                            "message", "You don't have permission to access this resource"));
        };
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}

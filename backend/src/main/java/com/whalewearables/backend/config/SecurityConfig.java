package com.whalewearables.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for development
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/home").permitAll() // ✅ allow this endpoint
                        .anyRequest().authenticated() // everything else needs auth
                )
                .httpBasic(Customizer.withDefaults()); // use basic auth for other endpoints

        return http.build();
    }
}

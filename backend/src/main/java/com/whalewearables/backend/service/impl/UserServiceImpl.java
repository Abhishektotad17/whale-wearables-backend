package com.whalewearables.backend.service.impl;

import com.whalewearables.backend.model.User;
import com.whalewearables.backend.repository.UserRepository;
import com.whalewearables.backend.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String identifier) {
        if (identifier.contains("@")) {
            return findByEmail(identifier);
        } else {
            return findByUsername(identifier);
        }
    }
    public Optional<User> findByUsername(String username) {
        return repo.findByName(username);
    }

    public User registerLocalUser(String name, String email, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(hashed);
        u.setProvider("local");

        return repo.save(u);
    }

    public User registerGoogleUser(String name, String email, String pictureUrl) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(null);         // Explicitly setting null
        u.setProvider("google");
        u.setPicture(pictureUrl);
        return repo.save(u);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .roles("USER")
                .build();
    }

    @Override
    public User save(User user) {
        return repo.save(user);
    }
}

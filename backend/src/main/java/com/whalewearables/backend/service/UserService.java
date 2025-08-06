package com.whalewearables.backend.service;

import com.whalewearables.backend.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User registerLocalUser(String name, String email, String rawPassword);

    User registerGoogleUser(String name, String email, String pictureUrl);

    User save(User user);
}

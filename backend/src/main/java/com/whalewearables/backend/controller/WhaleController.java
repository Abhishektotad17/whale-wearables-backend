package com.whalewearables.backend.controller;

import com.whalewearables.backend.model.HomeContent;
import com.whalewearables.backend.repository.WhaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class WhaleController {

    @Autowired
    private WhaleRepository repository;

    @GetMapping
    public ResponseEntity<HomeContent> getHomeContent() {
        return repository.findById(1L)  // Assuming one row
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.whalewearables.backend.repository;

import com.whalewearables.backend.model.HomeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WhaleRepository extends JpaRepository<HomeContent, Long> {
}

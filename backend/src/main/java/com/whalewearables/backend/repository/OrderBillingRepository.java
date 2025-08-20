package com.whalewearables.backend.repository;

import com.whalewearables.backend.model.OrderBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBillingRepository extends JpaRepository<OrderBilling, Long> {
}
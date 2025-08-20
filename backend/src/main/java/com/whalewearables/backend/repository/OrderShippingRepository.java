package com.whalewearables.backend.repository;

import com.whalewearables.backend.model.OrderShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderShippingRepository extends JpaRepository<OrderShipping, Long> {
}

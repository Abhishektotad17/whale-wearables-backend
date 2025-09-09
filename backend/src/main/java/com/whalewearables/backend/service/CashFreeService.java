package com.whalewearables.backend.service;

import com.cashfree.pg.model.OrderEntity;
import com.cashfree.pg.model.PaymentEntity;

import java.util.List;

public interface CashFreeService {

    String generateToken(String orderId);
    OrderEntity fetchOrderDetails(String orderId);
    List<PaymentEntity> fetchPayments(String orderId);
}

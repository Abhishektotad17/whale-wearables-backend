package com.whalewearables.backend.service;

public interface CashFreeService {

    String generateToken(String orderId);
    String updateOrderStatusFromGateway(String orderId);
}

package com.whalewearables.backend.dto;

import com.whalewearables.backend.model.PaymentMethod;
import com.whalewearables.backend.model.PaymentStatus;

import java.math.BigDecimal;

public class PaymentDTO {

    private String orderId;        // required
    private Long userId;        // optional if you derive from session
    private BigDecimal amount;     // required
    private String currency;       // default "INR"
    private PaymentStatus status;         // e.g., SUCCESS / FAILED / PENDING
    private PaymentMethod paymentMethod;  // e.g., CARD, UPI, NETBANKING
    private String transactionId;

    public PaymentDTO() {
    }

    public PaymentDTO(String orderId,
                      Long userId,
                      BigDecimal amount,
                      String currency,
                      PaymentStatus status,
                      PaymentMethod paymentMethod,
                      String transactionId) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
    }

    // Getters & Setters

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}

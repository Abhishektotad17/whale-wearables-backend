package com.whalewearables.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id", updatable = false, nullable = false)
    private String orderId;

    private BigDecimal amount;

    private String phone;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderBilling billing;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderShipping shipping;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payments> payments = new ArrayList<>();

    public Order() {
    }

    public Order(String orderId, BigDecimal amount, String phone, String status, LocalDateTime createdAt, LocalDateTime updatedAt, OrderBilling billing, OrderShipping shipping, List<OrderItem> items, List<Payments> payments) {
        this.orderId = orderId;
        this.amount = amount;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.billing = billing;
        this.shipping = shipping;
        this.items = items;
        this.payments = payments;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OrderBilling getBilling() {
        return billing;
    }

    public void setBilling(OrderBilling billing) {
        this.billing = billing;
    }

    public OrderShipping getShipping() {
        return shipping;
    }

    public void setShipping(OrderShipping shipping) {
        this.shipping = shipping;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<Payments> getPayments() {
        return payments;
    }

    public void setPayments(List<Payments> payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", billing=" + billing +
                ", shipping=" + shipping +
                ", items=" + items +
                ", payments=" + payments +
                '}';
    }
}

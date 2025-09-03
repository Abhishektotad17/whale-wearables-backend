package com.whalewearables.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderRequest {

    private BigDecimal amount;
    private String phone;

    private BillingDTO billing;
    private ShippingDTO shipping;
    private List<ItemDTO> items;

    public OrderRequest() {
    }

    public OrderRequest(BigDecimal amount, String phone, BillingDTO billing, ShippingDTO shipping, List<ItemDTO> items) {
        this.amount = amount;
        this.phone = phone;
        this.billing = billing;
        this.shipping = shipping;
        this.items = items;
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

    public BillingDTO getBilling() {
        return billing;
    }

    public void setBilling(BillingDTO billing) {
        this.billing = billing;
    }

    public ShippingDTO getShipping() {
        return shipping;
    }

    public void setShipping(ShippingDTO shipping) {
        this.shipping = shipping;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "amount=" + amount +
                ", phone='" + phone + '\'' +
                ", billing=" + billing +
                ", shipping=" + shipping +
                ", items=" + items +
                '}';
    }
}

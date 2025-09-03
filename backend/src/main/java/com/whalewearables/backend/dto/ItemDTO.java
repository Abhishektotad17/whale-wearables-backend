package com.whalewearables.backend.dto;

import java.math.BigDecimal;

public class ItemDTO {

    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;

    public ItemDTO() {
    }

    public ItemDTO(Long productId, Integer quantity, BigDecimal price, String productName) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productName='" + productName + '\'' +
                '}';
    }
}

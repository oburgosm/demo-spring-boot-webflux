package com.capgemini.demo.webflux.model.dto;

/**
 *
 * @author oburgosm
 */
public class OrderItemRequest {
    
    Long productId;
    Integer quantity;

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
    
    
    
}

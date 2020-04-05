package com.capgemini.demo.webflux.model.dto;

import java.util.List;

/**
 *
 * @author oburgosm
 */
public class OrderRequest {

    private Long customerID;
    private List<OrderItemRequest> orderItems;

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }

    

}

package com.capgemini.demo.webflux.model.dto;

import java.util.List;

import com.capgemini.demo.webflux.model.domain.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author oburgosm
 */
public class CustomerDTO {
    
    private Long id;

    private String name;

    @JsonIgnore
    private List<Order> orders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    
    
}

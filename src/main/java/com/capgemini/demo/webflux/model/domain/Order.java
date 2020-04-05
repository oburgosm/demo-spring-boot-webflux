package com.capgemini.demo.webflux.model.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;

/**
 *
 * @author oburgosm
 */
@Entity
@Table(name= "Orders")
public class Order implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = LAZY)
    private OrderItem productOrder;
    
    @ManyToOne(fetch = LAZY)
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItem getProductOrder() {
        return productOrder;
    }

    public void setProductOrder(OrderItem productOrder) {
        this.productOrder = productOrder;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Order other = (Order) obj;
        return Objects.equals(this.id, other.id);
    }
    
    
    
}

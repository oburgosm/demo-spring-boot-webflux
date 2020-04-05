package com.capgemini.demo.webflux.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.demo.webflux.model.domain.Order;

/**
 *
 * @author oburgosm
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    
}

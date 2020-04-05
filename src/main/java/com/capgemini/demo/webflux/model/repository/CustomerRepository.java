package com.capgemini.demo.webflux.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.demo.webflux.model.domain.Customer;

/**
 *
 * @author oburgosm
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
}

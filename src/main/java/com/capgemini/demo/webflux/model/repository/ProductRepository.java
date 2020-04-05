package com.capgemini.demo.webflux.model.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.demo.webflux.model.domain.Product;

/**
 *
 * @author oburgosm
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    public List<Product> findByNameOrderByName(String name);
    
    public List<Product> findAllByOrderByPrice();
    
    public List<Product> findAllByOrderByQuantity();
    
}

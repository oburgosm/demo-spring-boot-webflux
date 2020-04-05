package com.capgemini.demo.webflux.controller;

import java.util.SplittableRandom;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.demo.webflux.model.domain.Customer;
import com.capgemini.demo.webflux.model.domain.Product;
import com.capgemini.demo.webflux.model.repository.CustomerRepository;
import com.capgemini.demo.webflux.model.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller to admin data base
 * @author oburgosm
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Inject
    private ProductRepository productRepository;
    
    @Inject
    private CustomerRepository customerRepository;
    
    private final SplittableRandom splittableRandom = new SplittableRandom();
    
    /**
     * We insert some products in database
     * @return 
     */
    @PostMapping("init")
    public Mono<Void> initDataBase() {
        return initProducts().and(initCustomers());
    }
    
    private void insertNewProduct(String name, double price, Long quantity) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        this.productRepository.saveAndFlush(product);
    }
    
    private void insertNewCustomer(String name) {
        Customer customer = new Customer();
        customer.setName(name);
        this.customerRepository.saveAndFlush(customer);
    }

    private Mono<Void> initProducts() {
        return Flux.range(1, 1000).doOnNext((i) -> {
            this.insertNewProduct("Product"+i, this.splittableRandom.nextDouble(5d, 100d), this.splittableRandom.nextLong(0L, 100000L));
        }).then();
    }

    private Mono<Void> initCustomers() {
        return Flux.range(1, 100).doOnNext((i) -> {
            this.insertNewCustomer("Customer"+i);
        }).then();
    }
    
}

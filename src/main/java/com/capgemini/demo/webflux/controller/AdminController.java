package com.capgemini.demo.webflux.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.demo.webflux.model.domain.Customer;
import com.capgemini.demo.webflux.model.domain.Product;
import com.capgemini.demo.webflux.model.repository.CustomerRepository;
import com.capgemini.demo.webflux.model.repository.ProductRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    
    /**
     * We insert some products in database
     * @return 
     */
    @PostMapping("init")
    public Mono<Void> initDataBase() {
        Mono<Void> blockingWrapper = Mono.fromCallable(() -> {
            initProducts();
            initCustomers();
            return null;
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
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

    private void initProducts() {
        this.insertNewProduct("Camisa", 29.99, 1000L);
        this.insertNewProduct("Jersey", 39.99, 10000L);
        this.insertNewProduct("Pantalon", 49.95, 5000L);
    }

    private void initCustomers() {
        this.insertNewCustomer("Ana");
        this.insertNewCustomer("Juan");
        this.insertNewCustomer("Paco");
        this.insertNewCustomer("Maria");
    }
    
}

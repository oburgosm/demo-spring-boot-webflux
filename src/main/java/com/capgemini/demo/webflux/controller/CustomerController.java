package com.capgemini.demo.webflux.controller;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.capgemini.demo.webflux.model.domain.Customer;
import com.capgemini.demo.webflux.model.repository.CustomerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 *
 * @author oburgosm
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Inject
    private CustomerRepository customerRepository;

    @GetMapping
    public Flux<Customer> customers() {
        Mono<List<Customer>> blockingWrapper = Mono.fromCallable(() -> {
            return this.customerRepository.findAll();
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic()).flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/{id}")
    public Mono<Customer> customer(@PathVariable @NotNull Long id) {
        Mono<Customer> blockingWrapper = Mono.fromCallable(() -> {
            Optional<Customer> optionalCustomer = this.customerRepository.findById(id);
            if (!optionalCustomer.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "customer not found"
                );
            } else {
                return optionalCustomer.get();
            }
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    public Mono<Customer> insertCustomer(@RequestBody Customer customerDTO) {
        Mono<Customer> blockingWrapper = Mono.fromCallable(() -> {
            Customer newCustomer = new Customer();
            newCustomer.setName(customerDTO.getName());
            return this.customerRepository.saveAndFlush(newCustomer);
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping
    public Mono<Customer> updateCustomer(@RequestBody Customer customerDTO) {
        Mono<Customer> blockingWrapper = Mono.fromCallable(() -> {
            Optional<Customer> optionalCustomer = this.customerRepository.findById(customerDTO.getId());
            if (!optionalCustomer.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "customer not found"
                );
            }
            Customer customer = optionalCustomer.get();
            String name = customerDTO.getName();
            if (name != null) {
                customer.setName(name);
            }
            return this.customerRepository.saveAndFlush(customer);
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());

    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        Mono<Void> blockingWrapper = Mono.fromCallable(() -> {
            Optional<Customer> optionalCustomer = this.customerRepository.findById(id);
            if (!optionalCustomer.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "customer not found"
                );
            }
            Customer product = optionalCustomer.get();
            this.customerRepository.delete(product);
            return null;
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

}

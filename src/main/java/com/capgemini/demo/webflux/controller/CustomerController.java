package com.capgemini.demo.webflux.controller;

import java.util.List;

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

import com.capgemini.demo.webflux.mapper.CustomerMapper;
import com.capgemini.demo.webflux.model.domain.Customer;
import com.capgemini.demo.webflux.model.dto.CustomerDTO;
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

    @Inject
    private CustomerMapper customerMapper;

    @GetMapping
    public Flux<CustomerDTO> customers() {
        Mono<List<Customer>> blockingWrapper = Mono.fromCallable(() -> {
            return this.customerRepository.findAll();
        });
        return blockingWrapper
                .flatMapMany(Flux::fromIterable)
                .map(this.customerMapper::customerToCustomerDTO)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{id}")
    public Mono<CustomerDTO> customer(@PathVariable @NotNull Long id) {
        return Mono.just(id)
                .map(this.customerRepository::findById)
                .map(optional -> optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found")))
                .map(this.customerMapper::customerToCustomerDTO)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    public Mono<CustomerDTO> insertCustomer(@RequestBody CustomerDTO customerDTO) {
        return Mono.just(customerDTO)
                .map(this.customerMapper::customerDTOToCustomer)
                .map(this.customerRepository::saveAndFlush)
                .map(this.customerMapper::customerToCustomerDTO)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping
    public Mono<CustomerDTO> updateCustomer(@RequestBody CustomerDTO customerDTO) {
        return Mono.just(customerDTO)
                .map(c -> c.getId())
                .map(this.customerRepository::findById)
                .map(optional -> optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found")))
                .map(c -> {
                    String name = customerDTO.getName();
                    if (name != null) {
                        c.setName(name);
                    }
                    return c;
                })
                .map(this.customerRepository::saveAndFlush)
                .map(this.customerMapper::customerToCustomerDTO)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping("/{id}")
    public Mono<CustomerDTO> delete(@PathVariable Long id) {
        return Mono.just(id)
                .map(this.customerRepository::findById)
                .map(optional -> optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found")))
                .doOnNext(this.customerRepository::delete)
                .map(this.customerMapper::customerToCustomerDTO)
                .subscribeOn(Schedulers.boundedElastic());
    }

}

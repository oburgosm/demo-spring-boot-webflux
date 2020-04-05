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

import com.capgemini.demo.webflux.model.domain.Product;
import com.capgemini.demo.webflux.model.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 *
 * @author oburgosm
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    @Inject
    private ProductRepository productRepository;

    @GetMapping
    public Flux<Product> products() {
        Mono<List<Product>> blockingWrapper = Mono.fromCallable(() -> {
            return this.productRepository.findAll();
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic()).flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/{id}")
    public Mono<Product> product(@PathVariable @NotNull Long id) {
        Mono<Product> blockingWrapper = Mono.fromCallable(() -> {
            Optional<Product> optionalProduct = this.productRepository.findById(id);
            if (!optionalProduct.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "product not found"
                );
            } else {
                return optionalProduct.get();
            }
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    public Mono<Product> insertProduct(@RequestBody Product productDTO) {
        Mono<Product> blockingWrapper = Mono.fromCallable(() -> {
            Product newProduct = new Product();
            newProduct.setName(productDTO.getName());
            newProduct.setPrice(productDTO.getPrice());
            newProduct.setQuantity(productDTO.getQuantity());
            return this.productRepository.saveAndFlush(newProduct);
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping
    public Mono<Product> updateProduct(@RequestBody Product productDTO) {
        Mono<Product> blockingWrapper = Mono.fromCallable(() -> {
            Optional<Product> optionalProduct = this.productRepository.findById(productDTO.getId());
            if (!optionalProduct.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "product not found"
                );
            }
            Product product = optionalProduct.get();
            String name = productDTO.getName();
            if (name != null) {
                product.setName(name);
            }
            Double price = productDTO.getPrice();
            if (price != null) {
                product.setPrice(price);
            }
            Long quantity = productDTO.getQuantity();
            if (quantity != null) {
                product.setQuantity(quantity);
            }
            return this.productRepository.saveAndFlush(product);
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());

    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        Mono<Void> blockingWrapper = Mono.fromCallable(() -> {
            Optional<Product> optionalProduct = this.productRepository.findById(id);
            if (!optionalProduct.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "product not found"
                );
            }
            Product product = optionalProduct.get();
            this.productRepository.delete(product);
            return null;
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

}

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

import com.capgemini.demo.webflux.mapper.ProductMapper;
import com.capgemini.demo.webflux.model.domain.Product;
import com.capgemini.demo.webflux.model.dto.ProductDTO;
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

    @Inject
    private ProductMapper productMapper;

    @GetMapping
    public Flux<ProductDTO> products() {
        Mono<List<Product>> blockingWrapper = Mono.fromCallable(() -> {
            return this.productRepository.findAll();
        });
        return blockingWrapper
                .flatMapMany(Flux::fromIterable)
                .map(this.productMapper::productToProductDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{id}")
    public Mono<ProductDTO> product(@PathVariable @NotNull Long id) {
        return Mono.just(id)
                .map(this.productRepository::findById)
                .map((optional -> optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"))))
                .map(this.productMapper::productToProductDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    public Mono<ProductDTO> insertProduct(@RequestBody ProductDTO productDTO) {
        return Mono.just(productDTO)
                .map(this.productMapper::productDtoToProduct)
                .map(this.productRepository::saveAndFlush)
                .map(this.productMapper::productToProductDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Update only attributes of Product
     *
     * @param productDTO the product with ID and
     * @return
     */
    @PutMapping
    public Mono<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO) {
        return Mono.just(productDTO.getId())
                .map(this.productRepository::findById)
                .map((optional -> optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"))))
                .map((Product p) -> {
                    String name = productDTO.getName();
                    if (name != null) {
                        p.setName(name);
                    }
                    Double price = productDTO.getPrice();
                    if (price != null) {
                        p.setPrice(price);
                    }
                    Long quantity = productDTO.getQuantity();
                    if (quantity != null) {
                        p.setQuantity(quantity);
                    }
                    return p;
                })
                .map(this.productRepository::saveAndFlush)
                .map(this.productMapper::productToProductDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping("/{id}")
    public Mono<ProductDTO> delete(@PathVariable Long id) {
        return Mono.just(id)
                .map(this.productRepository::findById)
                .map((optional -> optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"))))
                .doOnNext(this.productRepository::delete)
                .map(this.productMapper::productToProductDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

}

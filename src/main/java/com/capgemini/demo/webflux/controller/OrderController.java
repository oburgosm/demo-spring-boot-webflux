package com.capgemini.demo.webflux.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.capgemini.demo.webflux.model.domain.Customer;
import com.capgemini.demo.webflux.model.domain.Order;
import com.capgemini.demo.webflux.model.domain.OrderItem;
import com.capgemini.demo.webflux.model.dto.OrderRequest;
import com.capgemini.demo.webflux.model.repository.CustomerRepository;
import com.capgemini.demo.webflux.model.repository.OrderRepository;
import com.capgemini.demo.webflux.model.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 *
 * @author oburgosm
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private CustomerRepository customerRepository;

    @Inject
    private ProductRepository productRepository;

    @GetMapping
    public Flux<Order> orders() {
        Mono<List<Order>> blockingWrapper = Mono.fromCallable(() -> {
            return this.orderRepository.findAll();
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic()).flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/{id}")
    public Mono<Order> order(@PathVariable @NotNull Long id) {
        Mono<Order> blockingWrapper = Mono.fromCallable(() -> {
            Optional<Order> optionalOrder = this.orderRepository.findById(id);
            if (!optionalOrder.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "order not found"
                );
            } else {
                return optionalOrder.get();
            }
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    public Mono<Order> insertOrder(@RequestBody OrderRequest orderDTO) {

        Mono<Order> order = Mono.fromSupplier(Order::new);

        Mono<Customer> customer = Mono.fromCallable(() -> {
            try {
                return this.customerRepository.findById(orderDTO.getCustomerID()).orElseThrow();
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found", e);
            }
        });

        Mono<List<OrderItem>> orderItems = Flux.fromIterable(orderDTO.getOrderItems()).parallel().runOn(Schedulers.boundedElastic()).map((orderItemRequest) -> {
            OrderItem orderItem = new OrderItem();
            this.productRepository.findById(orderItemRequest.getProductId()).ifPresentOrElse((p) -> {
                orderItem.setProduct(p);
                orderItem.setPrice(p.getPrice());
                Long stock = p.getQuantity();
                Integer quantity = orderItemRequest.getQuantity();
                Long newStock = stock - quantity;
                if (newStock < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is not enought stock");
                }
                orderItem.setQuantity(quantity);
                p.setQuantity(newStock);
                this.productRepository.save(p);
            }, () -> {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "product not found"
                );
            });
            return orderItem;
        }).sequential().collectList();

        return order.zipWith(customer, (o, c) -> {
            o.setCustomer(c);
            return o;
        }).zipWith(orderItems, (o, oi) -> {
            Double amount = oi.stream()
                    .map((act -> {
                        act.setOrder(o);
                        return act;
                    }))
                    .map((act) -> {
                        return act.getPrice() * act.getQuantity();
                    }).reduce(0.0, Double::sum);
            o.setAmount(amount);
            o.setOrderItems(oi);
            return o;
        }).map((o) -> {
            return this.orderRepository.saveAndFlush(o);
        });

//Declarative
//        Order newOrder = new Order();
//        Mono<Order> blockingWrapper = Mono.fromCallable(() -> {
//            this.customerRepository.findById(orderDTO.getCustomerID()).ifPresentOrElse((customer) -> {
//                newOrder.setCustomer(customer);
//            }, () -> {
//                throw new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "customer not found"
//                );
//            });
//            List<OrderItem> orderItems = new ArrayList<>();
//            for (OrderItemRequest orderItemRequest : orderDTO.getOrderItems()) {
//                OrderItem orderItem = new OrderItem();
//                this.productRepository.findById(orderItemRequest.getProductId()).ifPresentOrElse((p) -> {
//                    orderItem.setProduct(p);
//                    orderItem.setPrice(p.getPrice());
//                    orderItem.setOrder(newOrder);
//                }, () -> {
//                    throw new ResponseStatusException(
//                            HttpStatus.NOT_FOUND, "product not found"
//                    );
//                });
//                orderItem.setQuantity(orderItem.getQuantity());
//                orderItems.add(orderItem);
//            }
//            newOrder.setOrderItems(orderItems);
//            return this.orderRepository.saveAndFlush(newOrder);
//        });
//        return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
    }

}

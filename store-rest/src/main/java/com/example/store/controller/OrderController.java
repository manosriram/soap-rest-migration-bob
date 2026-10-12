package com.example.store.controller;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.exception.ConflictException;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.exception.UnprocessableEntityException;
import com.example.store.model.Order;
import com.example.store.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody CreateOrderRequest req) {
        Order order = orderService.placeOrder(req.productId(), req.customerName(), req.quantity());
        if (order == null) {
            throw new UnprocessableEntityException(
                    "Cannot place order: product " + req.productId() +
                    " not found or insufficient stock for quantity " + req.quantity() + ".");
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{orderId}").buildAndExpand(order.orderId()).toUri();
        return ResponseEntity.created(location).body(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable int orderId) {
        Order order = orderService.getOrder(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found.");
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<Order> listOrders() {
        return orderService.listOrders();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable int orderId) {
        Order existing = orderService.getOrder(orderId);
        if (existing == null) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found.");
        }
        boolean cancelled = orderService.cancelOrder(orderId);
        if (!cancelled) {
            throw new ConflictException("Order " + orderId + " is already cancelled.");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{orderId}/invoice", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generateInvoice(@PathVariable int orderId) {
        String invoice = orderService.generateInvoice(orderId);
        if (invoice.startsWith("ERROR")) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found.");
        }
        return ResponseEntity.ok(invoice);
    }
}

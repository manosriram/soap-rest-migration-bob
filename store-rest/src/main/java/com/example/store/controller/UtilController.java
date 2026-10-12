package com.example.store.controller;

import com.example.store.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/utils")
public class UtilController {

    private final OrderService orderService;

    UtilController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> sayHello(
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(Map.of("greeting", orderService.sayHello(name)));
    }

    @GetMapping("/discount")
    public ResponseEntity<Map<String, Double>> calculateDiscount(
            @RequestParam double price,
            @RequestParam double percent) {
        return ResponseEntity.ok(Map.of("discountedPrice",
                orderService.calculateDiscount(price, percent)));
    }

    @GetMapping("/currency")
    public ResponseEntity<Map<String, Double>> convertCurrency(
            @RequestParam double amountUSD,
            @RequestParam double rate) {
        return ResponseEntity.ok(Map.of("convertedAmount",
                orderService.convertCurrency(amountUSD, rate)));
    }

    @GetMapping("/tax")
    public ResponseEntity<Map<String, Double>> applyTax(
            @RequestParam double price,
            @RequestParam double taxRate) {
        return ResponseEntity.ok(Map.of("priceWithTax",
                orderService.applyTax(price, taxRate)));
    }
}

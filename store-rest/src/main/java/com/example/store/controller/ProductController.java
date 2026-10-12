package com.example.store.controller;

import com.example.store.dto.CreateProductRequest;
import com.example.store.dto.UpdatePriceRequest;
import com.example.store.exception.ConflictException;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.model.Product;
import com.example.store.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Void> addProduct(@RequestBody CreateProductRequest req) {
        boolean added = productService.addProduct(req.id(), req.name(), req.price(), req.quantity());
        if (!added) {
            throw new ConflictException("Product with id " + req.id() + " already exists or has invalid fields.");
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(req.id()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        Product product = productService.getProduct(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found.");
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public List<Product> listProducts() {
        return productService.listProducts();
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<Void> updateProductPrice(@PathVariable int id,
                                                   @RequestBody UpdatePriceRequest req) {
        if (req.newPrice() < 0) {
            throw new IllegalArgumentException("Price must not be negative.");
        }
        boolean updated = productService.updateProductPrice(id, req.newPrice());
        if (!updated) {
            throw new ResourceNotFoundException("Product with id " + id + " not found.");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Product with id " + id + " not found.");
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> checkStock(@PathVariable int id,
                                                          @RequestParam int requested) {
        Product product = productService.getProduct(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found.");
        }
        boolean inStock = productService.checkStock(id, requested);
        return ResponseEntity.ok(Map.of(
                "productId", id,
                "available", product.quantity(),
                "requested", requested,
                "inStock", inStock
        ));
    }
}

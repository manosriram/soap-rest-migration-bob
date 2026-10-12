package com.example.store.service;

import com.example.store.model.Product;

import java.util.List;

public interface ProductService {
    boolean addProduct(int id, String name, double price, int quantity);
    Product getProduct(int id);
    List<Product> listProducts();
    boolean updateProductPrice(int id, double newPrice);
    boolean deleteProduct(int id);
    boolean checkStock(int productId, int requested);
}

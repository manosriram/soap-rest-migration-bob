package com.example.store.service;

import com.example.store.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Thread-safe in-memory store
    private final List<Product> products = new CopyOnWriteArrayList<>();

    @Override
    public boolean addProduct(int id, String name, double price, int quantity) {
        for (Product p : products) {
            if (p.id() == id) {
                log.warn("[addProduct] WARN: product id={} already exists – rejected.", id);
                return false;
            }
        }
        if (price < 0 || quantity < 0) {
            log.warn("[addProduct] WARN: invalid price or quantity for id={}", id);
            return false;
        }
        Product product = new Product(id, name, price, quantity);
        products.add(product);
        log.info("[addProduct] Added: {}", product);
        return true;
    }

    @Override
    public Product getProduct(int id) {
        for (Product p : products) {
            if (p.id() == id) {
                log.info("[getProduct] Found: {}", p);
                return p;
            }
        }
        log.info("[getProduct] Product id={} not found.", id);
        return null;
    }

    @Override
    public List<Product> listProducts() {
        log.info("[listProducts] Returning {} product(s).", products.size());
        return new ArrayList<>(products);
    }

    @Override
    public boolean updateProductPrice(int id, double newPrice) {
        if (newPrice < 0) {
            log.warn("[updateProductPrice] WARN: negative price rejected.");
            return false;
        }
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.id() == id) {
                double old = p.price();
                products.set(i, p.withPrice(newPrice));
                log.info("[updateProductPrice] id={} price {} -> {}", id, old, newPrice);
                return true;
            }
        }
        log.info("[updateProductPrice] Product id={} not found.", id);
        return false;
    }

    @Override
    public boolean deleteProduct(int id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).id() == id) {
                Product removed = products.remove(i);
                log.info("[deleteProduct] Removed: {}", removed);
                return true;
            }
        }
        log.info("[deleteProduct] Product id={} not found.", id);
        return false;
    }

    /** Package-internal: used by OrderServiceImpl to deduct/restore stock. */
    void setProductQuantity(int id, int newQuantity) {
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.id() == id) {
                products.set(i, p.withQuantity(newQuantity));
                return;
            }
        }
    }

    @Override
    public boolean checkStock(int productId, int requested) {
        Product product = getProduct(productId);
        if (product == null) {
            log.info("[checkStock] Product id={} not found – stock unknown.", productId);
            return false;
        }
        boolean inStock = product.quantity() >= requested;
        log.info("[checkStock] Product id={}: available={}, requested={} -> inStock={}",
                productId, product.quantity(), requested, inStock);
        return inStock;
    }
}

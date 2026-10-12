package com.example.store.service;

import com.example.store.model.Order;
import com.example.store.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final ProductServiceImpl productService;
    private final List<Order> orders = new CopyOnWriteArrayList<>();
    private final AtomicInteger orderIdSeq = new AtomicInteger(1000);

    public OrderServiceImpl(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Override
    public String sayHello(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + name.trim() + "! Welcome to the Store REST service.";
    }

    @Override
    public Order placeOrder(int productId, String customerName, int quantity) {
        Product product = productService.getProduct(productId);
        if (product == null) {
            log.info("[placeOrder] Product id={} not found – cannot place order.", productId);
            return null;
        }
        if (product.quantity() < quantity) {
            log.info("[placeOrder] Insufficient stock: available={}, requested={}",
                    product.quantity(), quantity);
            return null;
        }
        productService.setProductQuantity(productId, product.quantity() - quantity);
        double amount = product.price() * quantity;
        int newOrderId = orderIdSeq.getAndIncrement();
        Order order = new Order(newOrderId, productId, customerName, amount, "PLACED");
        orders.add(order);
        log.info("[placeOrder] Created: {}", order);
        return order;
    }

    @Override
    public Order getOrder(int orderId) {
        for (Order o : orders) {
            if (o.orderId() == orderId) {
                log.info("[getOrder] Found: {}", o);
                return o;
            }
        }
        log.info("[getOrder] Order id={} not found.", orderId);
        return null;
    }

    @Override
    public List<Order> listOrders() {
        log.info("[listOrders] Returning {} order(s).", orders.size());
        return new ArrayList<>(orders);
    }

    @Override
    public boolean cancelOrder(int orderId) {
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            if (o.orderId() == orderId) {
                if ("CANCELLED".equals(o.status())) {
                    log.info("[cancelOrder] Order {} already cancelled.", orderId);
                    return false;
                }
                // restore stock
                Product product = productService.getProduct(o.productId());
                if (product != null) {
                    int restoredQty = (int) Math.round(o.amount() / product.price());
                    productService.setProductQuantity(product.id(), product.quantity() + restoredQty);
                    log.info("[cancelOrder] Restored {} unit(s) to product id={}", restoredQty, product.id());
                }
                orders.set(i, o.withStatus("CANCELLED"));
                log.info("[cancelOrder] Cancelled: {}", orders.get(i));
                return true;
            }
        }
        log.info("[cancelOrder] Order id={} not found.", orderId);
        return false;
    }

    @Override
    public String generateInvoice(int orderId) {
        Order order = getOrder(orderId);
        if (order == null) {
            return "ERROR: Order " + orderId + " not found.";
        }
        Product product = productService.getProduct(order.productId());
        String productName = product != null ? product.name() : "Unknown Product";

        return """
                ========================================
                             INVOICE
                ========================================
                Order ID    : %d
                Customer    : %s
                Product     : %s (id=%d)
                Amount      : $%.2f
                Status      : %s
                ========================================
                        Thank you for your order!
                ========================================
                """.formatted(order.orderId(), order.customerName(),
                productName, order.productId(), order.amount(), order.status());
    }

    @Override
    public double calculateDiscount(double originalPrice, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            log.warn("[calculateDiscount] WARN: discountPercent out of range [0,100]: {}", discountPercent);
            return originalPrice;
        }
        double discounted = originalPrice * (1.0 - discountPercent / 100.0);
        log.info("[calculateDiscount] {} @ {}% off = {}", originalPrice, discountPercent, discounted);
        return discounted;
    }

    @Override
    public double convertCurrency(double amountUSD, double rate) {
        if (rate <= 0) {
            log.warn("[convertCurrency] WARN: invalid rate {} – returning original amount.", rate);
            return amountUSD;
        }
        double converted = amountUSD * rate;
        log.info("[convertCurrency] ${} USD * {} = {}", amountUSD, rate, converted);
        return converted;
    }

    @Override
    public double applyTax(double price, double taxRate) {
        if (taxRate < 0) {
            log.warn("[applyTax] WARN: negative tax rate – returning original price.");
            return price;
        }
        double withTax = price * (1.0 + taxRate / 100.0);
        log.info("[applyTax] {} + {}% tax = {}", price, taxRate, withTax);
        return withTax;
    }
}

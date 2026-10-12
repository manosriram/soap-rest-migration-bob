package com.example.soap.service;

import com.example.soap.model.Order;
import com.example.soap.model.Product;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service Endpoint Implementation (SEI) for the Store SOAP service.
 *
 * All data is held in-memory so the server is self-contained.
 */
@WebService(
        endpointInterface = "com.example.soap.service.StoreService",
        serviceName       = "StoreService",
        portName          = "StoreServicePort",
        targetNamespace   = "http://example.com/soap/store"
)
public class StoreServiceImpl implements StoreService {

    // In-memory stores
    private final List<Product> products = new ArrayList<>();
    private final List<Order>   orders   = new ArrayList<>();

    // Auto-increment for order IDs
    private final AtomicInteger orderIdSeq = new AtomicInteger(1000);

    // ------------------------------------------------------------------ 1
    @Override
    public String sayHello(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + name.trim() + "! Welcome to the Store SOAP service.";
    }

    // ------------------------------------------------------------------ 2
    @Override
    public boolean addProduct(int id, String name, double price, int quantity) {
        // reject duplicate IDs
        for (Product p : products) {
            if (p.getId() == id) {
                System.out.println("[addProduct] WARN: product id=" + id + " already exists – rejected.");
                return false;
            }
        }
        if (price < 0 || quantity < 0) {
            System.out.println("[addProduct] WARN: invalid price or quantity for id=" + id);
            return false;
        }
        products.add(new Product(id, name, price, quantity));
        System.out.println("[addProduct] Added: " + products.get(products.size() - 1));
        return true;
    }

    // ------------------------------------------------------------------ 3
    @Override
    public Product getProduct(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                System.out.println("[getProduct] Found: " + p);
                return p;
            }
        }
        System.out.println("[getProduct] Product id=" + id + " not found.");
        return null;
    }

    // ------------------------------------------------------------------ 4
    @Override
    public List<Product> listProducts() {
        System.out.println("[listProducts] Returning " + products.size() + " product(s).");
        return new ArrayList<>(products);
    }

    // ------------------------------------------------------------------ 5
    @Override
    public boolean updateProductPrice(int id, double newPrice) {
        if (newPrice < 0) {
            System.out.println("[updateProductPrice] WARN: negative price rejected.");
            return false;
        }
        for (Product p : products) {
            if (p.getId() == id) {
                double old = p.getPrice();
                p.setPrice(newPrice);
                System.out.println("[updateProductPrice] id=" + id + " price " + old + " -> " + newPrice);
                return true;
            }
        }
        System.out.println("[updateProductPrice] Product id=" + id + " not found.");
        return false;
    }

    // ------------------------------------------------------------------ 6
    @Override
    public boolean deleteProduct(int id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                Product removed = products.remove(i);
                System.out.println("[deleteProduct] Removed: " + removed);
                return true;
            }
        }
        System.out.println("[deleteProduct] Product id=" + id + " not found.");
        return false;
    }

    // ------------------------------------------------------------------ 7
    @Override
    public Order placeOrder(int productId, String customerName, int quantity) {
        Product product = getProduct(productId);
        if (product == null) {
            System.out.println("[placeOrder] Product id=" + productId + " not found – cannot place order.");
            return null;
        }
        if (product.getQuantity() < quantity) {
            System.out.println("[placeOrder] Insufficient stock: available=" +
                    product.getQuantity() + ", requested=" + quantity);
            return null;
        }
        // deduct stock
        product.setQuantity(product.getQuantity() - quantity);
        double amount = product.getPrice() * quantity;
        int newOrderId = orderIdSeq.getAndIncrement();
        Order order = new Order(newOrderId, productId, customerName, amount, "PLACED");
        orders.add(order);
        System.out.println("[placeOrder] Created: " + order);
        return order;
    }

    // ------------------------------------------------------------------ 8
    @Override
    public Order getOrder(int orderId) {
        for (Order o : orders) {
            if (o.getOrderId() == orderId) {
                System.out.println("[getOrder] Found: " + o);
                return o;
            }
        }
        System.out.println("[getOrder] Order id=" + orderId + " not found.");
        return null;
    }

    // ------------------------------------------------------------------ 9
    @Override
    public List<Order> listOrders() {
        System.out.println("[listOrders] Returning " + orders.size() + " order(s).");
        return new ArrayList<>(orders);
    }

    // ----------------------------------------------------------------- 10
    @Override
    public boolean cancelOrder(int orderId) {
        for (Order o : orders) {
            if (o.getOrderId() == orderId) {
                if ("CANCELLED".equals(o.getStatus())) {
                    System.out.println("[cancelOrder] Order " + orderId + " already cancelled.");
                    return false;
                }
                // restore stock
                Product product = getProduct(o.getProductId());
                if (product != null) {
                    int restoredQty = (int) Math.round(o.getAmount() / product.getPrice());
                    product.setQuantity(product.getQuantity() + restoredQty);
                    System.out.println("[cancelOrder] Restored " + restoredQty + " unit(s) to product id=" + product.getId());
                }
                o.setStatus("CANCELLED");
                System.out.println("[cancelOrder] Cancelled: " + o);
                return true;
            }
        }
        System.out.println("[cancelOrder] Order id=" + orderId + " not found.");
        return false;
    }

    // ----------------------------------------------------------------- 11
    @Override
    public double calculateDiscount(double originalPrice, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            System.out.println("[calculateDiscount] WARN: discountPercent out of range [0,100]: " + discountPercent);
            return originalPrice;
        }
        double discounted = originalPrice * (1.0 - discountPercent / 100.0);
        System.out.printf("[calculateDiscount] %.2f @ %.1f%% off = %.2f%n", originalPrice, discountPercent, discounted);
        return discounted;
    }

    // ----------------------------------------------------------------- 12
    @Override
    public double convertCurrency(double amountUSD, double rate) {
        if (rate <= 0) {
            System.out.println("[convertCurrency] WARN: invalid rate " + rate + " – returning original amount.");
            return amountUSD;
        }
        double converted = amountUSD * rate;
        System.out.printf("[convertCurrency] $%.2f USD * %.4f = %.2f%n", amountUSD, rate, converted);
        return converted;
    }

    // ----------------------------------------------------------------- 13
    @Override
    public boolean checkStock(int productId, int requested) {
        Product product = getProduct(productId);
        if (product == null) {
            System.out.println("[checkStock] Product id=" + productId + " not found – stock unknown.");
            return false;
        }
        boolean inStock = product.getQuantity() >= requested;
        System.out.println("[checkStock] Product id=" + productId + ": available=" +
                product.getQuantity() + ", requested=" + requested + " -> inStock=" + inStock);
        return inStock;
    }

    // ----------------------------------------------------------------- 14
    @Override
    public double applyTax(double price, double taxRate) {
        if (taxRate < 0) {
            System.out.println("[applyTax] WARN: negative tax rate – returning original price.");
            return price;
        }
        double withTax = price * (1.0 + taxRate / 100.0);
        System.out.printf("[applyTax] %.2f + %.1f%% tax = %.2f%n", price, taxRate, withTax);
        return withTax;
    }

    // ----------------------------------------------------------------- 15
    @Override
    public String generateInvoice(int orderId) {
        Order order = getOrder(orderId);
        if (order == null) {
            return "ERROR: Order " + orderId + " not found.";
        }
        Product product = getProduct(order.getProductId());
        String productName = product != null ? product.getName() : "Unknown Product";

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("             INVOICE\n");
        sb.append("========================================\n");
        sb.append(String.format("Order ID    : %d%n",    order.getOrderId()));
        sb.append(String.format("Customer    : %s%n",    order.getCustomerName()));
        sb.append(String.format("Product     : %s (id=%d)%n", productName, order.getProductId()));
        sb.append(String.format("Amount      : $%.2f%n", order.getAmount()));
        sb.append(String.format("Status      : %s%n",    order.getStatus()));
        sb.append("========================================\n");
        sb.append("        Thank you for your order!\n");
        sb.append("========================================\n");

        System.out.println("[generateInvoice] Generated invoice for order " + orderId);
        return sb.toString();
    }
}

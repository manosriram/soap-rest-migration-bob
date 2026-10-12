package com.example.store.service;

import com.example.store.model.Order;
import com.example.store.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verbose unit tests for all order-related and utility operations.
 * Direct port of the JUnit 4 StoreServiceImplTest – now JUnit 5.
 */
class OrderServiceImplTest {

    private ProductServiceImpl productService;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl();
        orderService = new OrderServiceImpl(productService);
        System.out.println("\n========================================");
        System.out.println("  [setUp] Fresh service instances created.");
        System.out.println("========================================");
    }

    // ==================================================================
    // sayHello
    // ==================================================================

    @Test
    void sayHello_withName() {
        System.out.println("\n--- TEST: sayHello (with name) ---");
        String result = orderService.sayHello("Alice");
        System.out.println("  Input   : \"Alice\"");
        System.out.println("  Result  : " + result);
        assertNotNull(result, "sayHello must return a non-null string");
        assertTrue(result.contains("Alice"), "Greeting should contain the caller's name");
        System.out.println("  PASSED");
    }

    @Test
    void sayHello_withNullName() {
        System.out.println("\n--- TEST: sayHello (null name falls back to generic greeting) ---");
        String result = orderService.sayHello(null);
        System.out.println("  Input   : null");
        System.out.println("  Result  : " + result);
        assertNotNull(result, "sayHello must never return null");
        assertFalse(result.trim().isEmpty(), "Fallback greeting must not be blank");
        System.out.println("  PASSED");
    }

    @Test
    void sayHello_withEmptyName() {
        System.out.println("\n--- TEST: sayHello (empty string falls back to generic greeting) ---");
        String result = orderService.sayHello("   ");
        System.out.println("  Input   : \"   \"");
        System.out.println("  Result  : " + result);
        assertNotNull(result, "sayHello must never return null");
        assertFalse(result.trim().isEmpty(), "Fallback greeting must not be blank");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // placeOrder
    // ==================================================================

    @Test
    void placeOrder_success() {
        System.out.println("\n--- TEST: placeOrder (happy path) ---");
        productService.addProduct(1, "Laptop", 999.99, 10);
        Order order = orderService.placeOrder(1, "Bob Smith", 2);
        System.out.println("  Placing order: productId=1, customer=Bob Smith, qty=2");
        System.out.println("  Result  : " + order);
        assertNotNull(order, "placeOrder should return an Order object on success");
        assertEquals(1, order.productId(), "Order should reference product 1");
        assertEquals("Bob Smith", order.customerName(), "Customer name should match");
        assertEquals(1999.98, order.amount(), 0.01, "Order amount should be 2 * 999.99");
        assertEquals("PLACED", order.status(), "Order status should be PLACED");
        // Stock should be reduced
        assertEquals(8, productService.getProduct(1).quantity(), "Stock should be reduced by 2");
        System.out.println("  PASSED");
    }

    @Test
    void placeOrder_insufficientStock() {
        System.out.println("\n--- TEST: placeOrder (insufficient stock returns null) ---");
        productService.addProduct(2, "Tablet", 399.99, 3);
        Order order = orderService.placeOrder(2, "Carol", 10); // only 3 available
        System.out.println("  Placing order for qty=10 when only 3 in stock");
        System.out.println("  Result  : " + order);
        assertNull(order, "placeOrder should return null when stock is insufficient");
        assertEquals(3, productService.getProduct(2).quantity(), "Stock should be unchanged");
        System.out.println("  PASSED");
    }

    @Test
    void placeOrder_productNotFound() {
        System.out.println("\n--- TEST: placeOrder (product not found returns null) ---");
        Order order = orderService.placeOrder(999, "Dave", 1);
        System.out.println("  Placing order for non-existent productId=999");
        System.out.println("  Result  : " + order);
        assertNull(order, "placeOrder should return null when the product does not exist");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // getOrder
    // ==================================================================

    @Test
    void getOrder_existingOrder() {
        System.out.println("\n--- TEST: getOrder (existing order) ---");
        productService.addProduct(1, "Laptop", 999.99, 5);
        Order placed = orderService.placeOrder(1, "Eve", 1);
        assertNotNull(placed, "Prerequisite: order must be placed successfully");

        Order fetched = orderService.getOrder(placed.orderId());
        System.out.println("  Fetching orderId=" + placed.orderId());
        System.out.println("  Result  : " + fetched);
        assertNotNull(fetched, "getOrder should return the order for a valid id");
        assertEquals(placed.orderId(), fetched.orderId(), "Order IDs should match");
        System.out.println("  PASSED");
    }

    @Test
    void getOrder_nonExistent() {
        System.out.println("\n--- TEST: getOrder (non-existent order returns null) ---");
        Order order = orderService.getOrder(99999);
        System.out.println("  Fetching orderId=99999 (never placed)");
        System.out.println("  Result  : " + order);
        assertNull(order, "getOrder should return null for unknown order id");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // listOrders
    // ==================================================================

    @Test
    void listOrders_empty() {
        System.out.println("\n--- TEST: listOrders (no orders placed) ---");
        List<Order> list = orderService.listOrders();
        System.out.println("  No orders placed yet");
        System.out.println("  Result size : " + list.size());
        assertNotNull(list, "listOrders must return a non-null list");
        assertTrue(list.isEmpty(), "Order list should be empty initially");
        System.out.println("  PASSED");
    }

    @Test
    void listOrders_afterPlacing() {
        System.out.println("\n--- TEST: listOrders (after placing 2 orders) ---");
        productService.addProduct(1, "Laptop",  999.99, 10);
        productService.addProduct(2, "Monitor", 299.99, 10);
        orderService.placeOrder(1, "Alice", 1);
        orderService.placeOrder(2, "Bob",   2);
        List<Order> list = orderService.listOrders();
        System.out.println("  Orders in list: " + list.size());
        list.forEach(o -> System.out.println("    -> " + o));
        assertEquals(2, list.size(), "Should return 2 orders");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // cancelOrder
    // ==================================================================

    @Test
    void cancelOrder_success() {
        System.out.println("\n--- TEST: cancelOrder (happy path) ---");
        productService.addProduct(1, "Laptop", 1000.0, 5);
        Order order = orderService.placeOrder(1, "Frank", 2);
        assertNotNull(order, "Prerequisite: order must be placed");

        int stockBefore = productService.getProduct(1).quantity(); // should be 3
        boolean cancelled = orderService.cancelOrder(order.orderId());
        System.out.println("  Cancelling orderId=" + order.orderId());
        System.out.println("  Result  : " + cancelled);
        assertTrue(cancelled, "cancelOrder should return true for a PLACED order");
        assertEquals("CANCELLED", orderService.getOrder(order.orderId()).status(),
                "Order status should be CANCELLED");
        assertTrue(productService.getProduct(1).quantity() >= stockBefore,
                "Stock should be at least as high as before cancellation");
        System.out.println("  PASSED");
    }

    @Test
    void cancelOrder_alreadyCancelled() {
        System.out.println("\n--- TEST: cancelOrder (already-cancelled order returns false) ---");
        productService.addProduct(1, "Laptop", 1000.0, 5);
        Order order = orderService.placeOrder(1, "Grace", 1);
        orderService.cancelOrder(order.orderId()); // first cancel
        boolean secondCancel = orderService.cancelOrder(order.orderId());
        System.out.println("  Second cancel of orderId=" + order.orderId());
        System.out.println("  Result  : " + secondCancel);
        assertFalse(secondCancel, "cancelOrder should return false if already cancelled");
        System.out.println("  PASSED");
    }

    @Test
    void cancelOrder_nonExistentOrder() {
        System.out.println("\n--- TEST: cancelOrder (non-existent order returns false) ---");
        boolean cancelled = orderService.cancelOrder(77777);
        System.out.println("  Cancelling orderId=77777 (never placed)");
        System.out.println("  Result  : " + cancelled);
        assertFalse(cancelled, "cancelOrder should return false for unknown order id");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // calculateDiscount
    // ==================================================================

    @Test
    void calculateDiscount_tenPercent() {
        System.out.println("\n--- TEST: calculateDiscount (10% off $200) ---");
        double result = orderService.calculateDiscount(200.0, 10.0);
        System.out.println("  Input   : price=200.0, discount=10%");
        System.out.println("  Result  : " + result);
        assertEquals(180.0, result, 0.001, "10% off $200 should be $180");
        System.out.println("  PASSED");
    }

    @Test
    void calculateDiscount_zeroPercent() {
        System.out.println("\n--- TEST: calculateDiscount (0% discount – price unchanged) ---");
        double result = orderService.calculateDiscount(150.0, 0.0);
        System.out.println("  Input   : price=150.0, discount=0%");
        System.out.println("  Result  : " + result);
        assertEquals(150.0, result, 0.001, "0% discount should return original price");
        System.out.println("  PASSED");
    }

    @Test
    void calculateDiscount_outOfRangePercent() {
        System.out.println("\n--- TEST: calculateDiscount (>100% discount returns original price) ---");
        double result = orderService.calculateDiscount(100.0, 110.0);
        System.out.println("  Input   : price=100.0, discount=110% (invalid)");
        System.out.println("  Result  : " + result);
        assertEquals(100.0, result, 0.001, "Out-of-range discount should return original price");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // convertCurrency
    // ==================================================================

    @Test
    void convertCurrency_usdToEur() {
        System.out.println("\n--- TEST: convertCurrency (USD -> EUR @ 0.92) ---");
        double result = orderService.convertCurrency(100.0, 0.92);
        System.out.println("  Input   : $100.00 USD, rate=0.92");
        System.out.println("  Result  : " + result);
        assertEquals(92.0, result, 0.001, "$100 * 0.92 should equal 92.0");
        System.out.println("  PASSED");
    }

    @Test
    void convertCurrency_rateOfOne() {
        System.out.println("\n--- TEST: convertCurrency (rate=1.0 – amount unchanged) ---");
        double result = orderService.convertCurrency(250.0, 1.0);
        System.out.println("  Input   : $250.00 USD, rate=1.0");
        System.out.println("  Result  : " + result);
        assertEquals(250.0, result, 0.001, "Rate 1.0 should return the same amount");
        System.out.println("  PASSED");
    }

    @Test
    void convertCurrency_invalidRate() {
        System.out.println("\n--- TEST: convertCurrency (invalid rate<=0 returns original) ---");
        double result = orderService.convertCurrency(100.0, 0.0);
        System.out.println("  Input   : $100.00 USD, rate=0.0 (invalid)");
        System.out.println("  Result  : " + result);
        assertEquals(100.0, result, 0.001, "Invalid rate should return original amount");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // applyTax
    // ==================================================================

    @Test
    void applyTax_tenPercent() {
        System.out.println("\n--- TEST: applyTax (10% tax on $50) ---");
        double result = orderService.applyTax(50.0, 10.0);
        System.out.println("  Input   : price=50.0, taxRate=10%");
        System.out.println("  Result  : " + result);
        assertEquals(55.0, result, 0.001, "$50 + 10% tax should be $55");
        System.out.println("  PASSED");
    }

    @Test
    void applyTax_zeroRate() {
        System.out.println("\n--- TEST: applyTax (0% tax – price unchanged) ---");
        double result = orderService.applyTax(75.0, 0.0);
        System.out.println("  Input   : price=75.0, taxRate=0%");
        System.out.println("  Result  : " + result);
        assertEquals(75.0, result, 0.001, "0% tax should leave price unchanged");
        System.out.println("  PASSED");
    }

    @Test
    void applyTax_negativeTaxRate() {
        System.out.println("\n--- TEST: applyTax (negative tax rate – returns original price) ---");
        double result = orderService.applyTax(100.0, -5.0);
        System.out.println("  Input   : price=100.0, taxRate=-5% (invalid)");
        System.out.println("  Result  : " + result);
        assertEquals(100.0, result, 0.001, "Negative tax rate should return original price");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // generateInvoice
    // ==================================================================

    @Test
    void generateInvoice_validOrder() {
        System.out.println("\n--- TEST: generateInvoice (valid order produces invoice) ---");
        productService.addProduct(1, "Monitor", 299.99, 5);
        Order order = orderService.placeOrder(1, "Heidi", 1);
        assertNotNull(order, "Prerequisite: order must be placed");

        String invoice = orderService.generateInvoice(order.orderId());
        System.out.println("  Generated invoice:\n" + invoice);
        assertNotNull(invoice, "Invoice must not be null");
        assertTrue(invoice.contains(String.valueOf(order.orderId())),
                "Invoice should contain the order id");
        assertTrue(invoice.contains("Heidi"), "Invoice should contain the customer name");
        assertTrue(invoice.contains("Monitor"), "Invoice should contain the product name");
        System.out.println("  PASSED");
    }

    @Test
    void generateInvoice_cancelledOrder() {
        System.out.println("\n--- TEST: generateInvoice (cancelled order still generates invoice) ---");
        productService.addProduct(2, "Keyboard", 49.99, 10);
        Order order = orderService.placeOrder(2, "Ivan", 1);
        orderService.cancelOrder(order.orderId());

        String invoice = orderService.generateInvoice(order.orderId());
        System.out.println("  Generated invoice for cancelled order:\n" + invoice);
        assertNotNull(invoice, "Invoice must not be null even for cancelled orders");
        assertTrue(invoice.contains("CANCELLED"), "Invoice should reflect CANCELLED status");
        System.out.println("  PASSED");
    }

    @Test
    void generateInvoice_nonExistentOrder() {
        System.out.println("\n--- TEST: generateInvoice (non-existent order returns error message) ---");
        String invoice = orderService.generateInvoice(55555);
        System.out.println("  orderId=55555 (never placed)");
        System.out.println("  Result  : " + invoice);
        assertNotNull(invoice, "Result must not be null for unknown order");
        assertTrue(invoice.startsWith("ERROR"), "Should contain ERROR indicator");
        System.out.println("  PASSED");
    }
}

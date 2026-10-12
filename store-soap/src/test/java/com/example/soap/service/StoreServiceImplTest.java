package com.example.soap.service;

import com.example.soap.model.Order;
import com.example.soap.model.Product;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Verbose unit tests for all 15 StoreService operations.
 *
 * Each test method:
 *  - describes its intent in a banner printed to stdout
 *  - exercises the happy-path and at least one edge/error case
 *  - uses expressive assertion messages so failures are self-explanatory
 */
public class StoreServiceImplTest {

    private StoreService service;

    /** Create a fresh service instance before every test (isolated state). */
    @Before
    public void setUp() {
        service = new StoreServiceImpl();
        System.out.println("\n========================================");
        System.out.println("  [setUp] Fresh StoreServiceImpl created.");
        System.out.println("========================================");
    }

    // ==================================================================
    // 1. sayHello
    // ==================================================================

    @Test
    public void testSayHello_withName() {
        System.out.println("\n--- TEST: sayHello (with name) ---");
        String result = service.sayHello("Alice");
        System.out.println("  Input   : \"Alice\"");
        System.out.println("  Result  : " + result);
        assertNotNull("sayHello must return a non-null string", result);
        assertTrue("Greeting should contain the caller's name",
                result.contains("Alice"));
        System.out.println("  PASSED");
    }

    @Test
    public void testSayHello_withNullName() {
        System.out.println("\n--- TEST: sayHello (null name falls back to generic greeting) ---");
        String result = service.sayHello(null);
        System.out.println("  Input   : null");
        System.out.println("  Result  : " + result);
        assertNotNull("sayHello must never return null", result);
        assertFalse("Fallback greeting must not be blank", result.trim().isEmpty());
        System.out.println("  PASSED");
    }

    @Test
    public void testSayHello_withEmptyName() {
        System.out.println("\n--- TEST: sayHello (empty string falls back to generic greeting) ---");
        String result = service.sayHello("   ");
        System.out.println("  Input   : \"   \"");
        System.out.println("  Result  : " + result);
        assertNotNull("sayHello must never return null", result);
        assertFalse("Fallback greeting must not be blank", result.trim().isEmpty());
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 2. addProduct
    // ==================================================================

    @Test
    public void testAddProduct_success() {
        System.out.println("\n--- TEST: addProduct (happy path) ---");
        boolean added = service.addProduct(1, "Laptop", 999.99, 10);
        System.out.println("  Adding id=1, name=Laptop, price=999.99, qty=10");
        System.out.println("  Result  : " + added);
        assertTrue("addProduct should return true for a valid new product", added);
        System.out.println("  PASSED");
    }

    @Test
    public void testAddProduct_duplicateIdRejected() {
        System.out.println("\n--- TEST: addProduct (duplicate id rejected) ---");
        service.addProduct(1, "Laptop", 999.99, 10);
        boolean addedAgain = service.addProduct(1, "Laptop Clone", 899.99, 5);
        System.out.println("  Trying to add duplicate id=1");
        System.out.println("  Result  : " + addedAgain);
        assertFalse("addProduct should return false when the id already exists", addedAgain);
        System.out.println("  PASSED");
    }

    @Test
    public void testAddProduct_negativePrice_rejected() {
        System.out.println("\n--- TEST: addProduct (negative price rejected) ---");
        boolean added = service.addProduct(2, "Ghost Product", -5.0, 1);
        System.out.println("  Adding id=2, price=-5.0");
        System.out.println("  Result  : " + added);
        assertFalse("addProduct should reject negative price", added);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 3. getProduct
    // ==================================================================

    @Test
    public void testGetProduct_existingProduct() {
        System.out.println("\n--- TEST: getProduct (existing product) ---");
        service.addProduct(10, "Keyboard", 49.99, 50);
        Product p = service.getProduct(10);
        System.out.println("  Fetching id=10");
        System.out.println("  Result  : " + p);
        assertNotNull("getProduct should return a Product for an existing id", p);
        assertEquals("Product id should match", 10, p.getId());
        assertEquals("Product name should match", "Keyboard", p.getName());
        assertEquals("Product price should match", 49.99, p.getPrice(), 0.001);
        assertEquals("Product quantity should match", 50, p.getQuantity());
        System.out.println("  PASSED");
    }

    @Test
    public void testGetProduct_nonExistent_returnsNull() {
        System.out.println("\n--- TEST: getProduct (non-existent id returns null) ---");
        Product p = service.getProduct(9999);
        System.out.println("  Fetching id=9999 (not added)");
        System.out.println("  Result  : " + p);
        assertNull("getProduct should return null when the product does not exist", p);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 4. listProducts
    // ==================================================================

    @Test
    public void testListProducts_empty() {
        System.out.println("\n--- TEST: listProducts (empty catalogue) ---");
        List<Product> list = service.listProducts();
        System.out.println("  No products added yet");
        System.out.println("  Result size : " + list.size());
        assertNotNull("listProducts must return a non-null list", list);
        assertTrue("Catalogue should be empty initially", list.isEmpty());
        System.out.println("  PASSED");
    }

    @Test
    public void testListProducts_afterAddingMultiple() {
        System.out.println("\n--- TEST: listProducts (after adding 3 products) ---");
        service.addProduct(1, "Mouse",    25.0, 100);
        service.addProduct(2, "Monitor", 300.0,  20);
        service.addProduct(3, "Desk",    150.0,   5);
        List<Product> list = service.listProducts();
        System.out.println("  Products in list: " + list.size());
        list.forEach(pr -> System.out.println("    -> " + pr));
        assertEquals("Should return 3 products", 3, list.size());
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 5. updateProductPrice
    // ==================================================================

    @Test
    public void testUpdateProductPrice_success() {
        System.out.println("\n--- TEST: updateProductPrice (happy path) ---");
        service.addProduct(5, "USB Hub", 19.99, 30);
        boolean updated = service.updateProductPrice(5, 24.99);
        System.out.println("  Updating id=5: old=19.99, new=24.99");
        System.out.println("  Result  : " + updated);
        assertTrue("updateProductPrice should succeed for existing id", updated);
        assertEquals("Price should now be 24.99", 24.99, service.getProduct(5).getPrice(), 0.001);
        System.out.println("  PASSED");
    }

    @Test
    public void testUpdateProductPrice_nonExistentProduct() {
        System.out.println("\n--- TEST: updateProductPrice (non-existent product returns false) ---");
        boolean updated = service.updateProductPrice(999, 50.0);
        System.out.println("  Trying to update id=999 (not added)");
        System.out.println("  Result  : " + updated);
        assertFalse("updateProductPrice should return false for missing id", updated);
        System.out.println("  PASSED");
    }

    @Test
    public void testUpdateProductPrice_negativePrice() {
        System.out.println("\n--- TEST: updateProductPrice (negative new price rejected) ---");
        service.addProduct(5, "USB Hub", 19.99, 30);
        boolean updated = service.updateProductPrice(5, -1.0);
        System.out.println("  Trying to set price=-1.0 for id=5");
        System.out.println("  Result  : " + updated);
        assertFalse("updateProductPrice should reject a negative new price", updated);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 6. deleteProduct
    // ==================================================================

    @Test
    public void testDeleteProduct_success() {
        System.out.println("\n--- TEST: deleteProduct (happy path) ---");
        service.addProduct(7, "Webcam", 59.99, 15);
        boolean deleted = service.deleteProduct(7);
        System.out.println("  Deleting id=7");
        System.out.println("  Result  : " + deleted);
        assertTrue("deleteProduct should return true for existing id", deleted);
        assertNull("Product should be gone after deletion", service.getProduct(7));
        assertEquals("Catalogue should be empty after deletion", 0, service.listProducts().size());
        System.out.println("  PASSED");
    }

    @Test
    public void testDeleteProduct_nonExistent() {
        System.out.println("\n--- TEST: deleteProduct (non-existent product returns false) ---");
        boolean deleted = service.deleteProduct(42);
        System.out.println("  Deleting id=42 (not added)");
        System.out.println("  Result  : " + deleted);
        assertFalse("deleteProduct should return false for missing id", deleted);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 7. placeOrder
    // ==================================================================

    @Test
    public void testPlaceOrder_success() {
        System.out.println("\n--- TEST: placeOrder (happy path) ---");
        service.addProduct(1, "Laptop", 999.99, 10);
        Order order = service.placeOrder(1, "Bob Smith", 2);
        System.out.println("  Placing order: productId=1, customer=Bob Smith, qty=2");
        System.out.println("  Result  : " + order);
        assertNotNull("placeOrder should return an Order object on success", order);
        assertEquals("Order should reference product 1", 1, order.getProductId());
        assertEquals("Customer name should match", "Bob Smith", order.getCustomerName());
        assertEquals("Order amount should be 2 * 999.99", 1999.98, order.getAmount(), 0.01);
        assertEquals("Order status should be PLACED", "PLACED", order.getStatus());
        // Stock should be reduced
        assertEquals("Stock should be reduced by 2", 8, service.getProduct(1).getQuantity());
        System.out.println("  PASSED");
    }

    @Test
    public void testPlaceOrder_insufficientStock() {
        System.out.println("\n--- TEST: placeOrder (insufficient stock returns null) ---");
        service.addProduct(2, "Tablet", 399.99, 3);
        Order order = service.placeOrder(2, "Carol", 10); // only 3 available
        System.out.println("  Placing order for qty=10 when only 3 in stock");
        System.out.println("  Result  : " + order);
        assertNull("placeOrder should return null when stock is insufficient", order);
        assertEquals("Stock should be unchanged", 3, service.getProduct(2).getQuantity());
        System.out.println("  PASSED");
    }

    @Test
    public void testPlaceOrder_productNotFound() {
        System.out.println("\n--- TEST: placeOrder (product not found returns null) ---");
        Order order = service.placeOrder(999, "Dave", 1);
        System.out.println("  Placing order for non-existent productId=999");
        System.out.println("  Result  : " + order);
        assertNull("placeOrder should return null when the product does not exist", order);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 8. getOrder
    // ==================================================================

    @Test
    public void testGetOrder_existingOrder() {
        System.out.println("\n--- TEST: getOrder (existing order) ---");
        service.addProduct(1, "Laptop", 999.99, 5);
        Order placed = service.placeOrder(1, "Eve", 1);
        assertNotNull("Prerequisite: order must be placed successfully", placed);

        Order fetched = service.getOrder(placed.getOrderId());
        System.out.println("  Fetching orderId=" + placed.getOrderId());
        System.out.println("  Result  : " + fetched);
        assertNotNull("getOrder should return the order for a valid id", fetched);
        assertEquals("Order IDs should match", placed.getOrderId(), fetched.getOrderId());
        System.out.println("  PASSED");
    }

    @Test
    public void testGetOrder_nonExistent() {
        System.out.println("\n--- TEST: getOrder (non-existent order returns null) ---");
        Order order = service.getOrder(99999);
        System.out.println("  Fetching orderId=99999 (never placed)");
        System.out.println("  Result  : " + order);
        assertNull("getOrder should return null for unknown order id", order);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 9. listOrders
    // ==================================================================

    @Test
    public void testListOrders_empty() {
        System.out.println("\n--- TEST: listOrders (no orders placed) ---");
        List<Order> list = service.listOrders();
        System.out.println("  No orders placed yet");
        System.out.println("  Result size : " + list.size());
        assertNotNull("listOrders must return a non-null list", list);
        assertTrue("Order list should be empty initially", list.isEmpty());
        System.out.println("  PASSED");
    }

    @Test
    public void testListOrders_afterPlacing() {
        System.out.println("\n--- TEST: listOrders (after placing 2 orders) ---");
        service.addProduct(1, "Laptop",  999.99, 10);
        service.addProduct(2, "Monitor", 299.99, 10);
        service.placeOrder(1, "Alice", 1);
        service.placeOrder(2, "Bob",   2);
        List<Order> list = service.listOrders();
        System.out.println("  Orders in list: " + list.size());
        list.forEach(o -> System.out.println("    -> " + o));
        assertEquals("Should return 2 orders", 2, list.size());
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 10. cancelOrder
    // ==================================================================

    @Test
    public void testCancelOrder_success() {
        System.out.println("\n--- TEST: cancelOrder (happy path) ---");
        service.addProduct(1, "Laptop", 1000.0, 5);
        Order order = service.placeOrder(1, "Frank", 2);
        assertNotNull("Prerequisite: order must be placed", order);

        int stockBefore = service.getProduct(1).getQuantity(); // should be 3
        boolean cancelled = service.cancelOrder(order.getOrderId());
        System.out.println("  Cancelling orderId=" + order.getOrderId());
        System.out.println("  Result  : " + cancelled);
        assertTrue("cancelOrder should return true for a PLACED order", cancelled);
        assertEquals("Order status should be CANCELLED", "CANCELLED",
                service.getOrder(order.getOrderId()).getStatus());
        assertTrue("Stock should be at least as high as before cancellation",
                service.getProduct(1).getQuantity() >= stockBefore);
        System.out.println("  PASSED");
    }

    @Test
    public void testCancelOrder_alreadyCancelled() {
        System.out.println("\n--- TEST: cancelOrder (already-cancelled order returns false) ---");
        service.addProduct(1, "Laptop", 1000.0, 5);
        Order order = service.placeOrder(1, "Grace", 1);
        service.cancelOrder(order.getOrderId()); // first cancel
        boolean secondCancel = service.cancelOrder(order.getOrderId());
        System.out.println("  Second cancel of orderId=" + order.getOrderId());
        System.out.println("  Result  : " + secondCancel);
        assertFalse("cancelOrder should return false if already cancelled", secondCancel);
        System.out.println("  PASSED");
    }

    @Test
    public void testCancelOrder_nonExistentOrder() {
        System.out.println("\n--- TEST: cancelOrder (non-existent order returns false) ---");
        boolean cancelled = service.cancelOrder(77777);
        System.out.println("  Cancelling orderId=77777 (never placed)");
        System.out.println("  Result  : " + cancelled);
        assertFalse("cancelOrder should return false for unknown order id", cancelled);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 11. calculateDiscount
    // ==================================================================

    @Test
    public void testCalculateDiscount_tenPercent() {
        System.out.println("\n--- TEST: calculateDiscount (10% off $200) ---");
        double result = service.calculateDiscount(200.0, 10.0);
        System.out.println("  Input   : price=200.0, discount=10%");
        System.out.println("  Result  : " + result);
        assertEquals("10% off $200 should be $180", 180.0, result, 0.001);
        System.out.println("  PASSED");
    }

    @Test
    public void testCalculateDiscount_zeroPercent() {
        System.out.println("\n--- TEST: calculateDiscount (0% discount – price unchanged) ---");
        double result = service.calculateDiscount(150.0, 0.0);
        System.out.println("  Input   : price=150.0, discount=0%");
        System.out.println("  Result  : " + result);
        assertEquals("0% discount should return original price", 150.0, result, 0.001);
        System.out.println("  PASSED");
    }

    @Test
    public void testCalculateDiscount_outOfRangePercent() {
        System.out.println("\n--- TEST: calculateDiscount (>100% discount returns original price) ---");
        double result = service.calculateDiscount(100.0, 110.0);
        System.out.println("  Input   : price=100.0, discount=110% (invalid)");
        System.out.println("  Result  : " + result);
        assertEquals("Out-of-range discount should return original price", 100.0, result, 0.001);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 12. convertCurrency
    // ==================================================================

    @Test
    public void testConvertCurrency_usdToEur() {
        System.out.println("\n--- TEST: convertCurrency (USD -> EUR @ 0.92) ---");
        double result = service.convertCurrency(100.0, 0.92);
        System.out.println("  Input   : $100.00 USD, rate=0.92");
        System.out.println("  Result  : " + result);
        assertEquals("$100 * 0.92 should equal 92.0", 92.0, result, 0.001);
        System.out.println("  PASSED");
    }

    @Test
    public void testConvertCurrency_rateOfOne() {
        System.out.println("\n--- TEST: convertCurrency (rate=1.0 – amount unchanged) ---");
        double result = service.convertCurrency(250.0, 1.0);
        System.out.println("  Input   : $250.00 USD, rate=1.0");
        System.out.println("  Result  : " + result);
        assertEquals("Rate 1.0 should return the same amount", 250.0, result, 0.001);
        System.out.println("  PASSED");
    }

    @Test
    public void testConvertCurrency_invalidRate() {
        System.out.println("\n--- TEST: convertCurrency (invalid rate<=0 returns original) ---");
        double result = service.convertCurrency(100.0, 0.0);
        System.out.println("  Input   : $100.00 USD, rate=0.0 (invalid)");
        System.out.println("  Result  : " + result);
        assertEquals("Invalid rate should return original amount", 100.0, result, 0.001);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 13. checkStock
    // ==================================================================

    @Test
    public void testCheckStock_sufficient() {
        System.out.println("\n--- TEST: checkStock (sufficient stock) ---");
        service.addProduct(1, "Chair", 89.0, 20);
        boolean inStock = service.checkStock(1, 5);
        System.out.println("  Stock=20, requested=5");
        System.out.println("  Result  : " + inStock);
        assertTrue("checkStock should return true when stock >= requested", inStock);
        System.out.println("  PASSED");
    }

    @Test
    public void testCheckStock_insufficient() {
        System.out.println("\n--- TEST: checkStock (insufficient stock) ---");
        service.addProduct(1, "Chair", 89.0, 2);
        boolean inStock = service.checkStock(1, 10);
        System.out.println("  Stock=2, requested=10");
        System.out.println("  Result  : " + inStock);
        assertFalse("checkStock should return false when stock < requested", inStock);
        System.out.println("  PASSED");
    }

    @Test
    public void testCheckStock_productNotFound() {
        System.out.println("\n--- TEST: checkStock (product not found returns false) ---");
        boolean inStock = service.checkStock(999, 1);
        System.out.println("  Product id=999 not in catalogue");
        System.out.println("  Result  : " + inStock);
        assertFalse("checkStock should return false if the product is unknown", inStock);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 14. applyTax
    // ==================================================================

    @Test
    public void testApplyTax_tenPercent() {
        System.out.println("\n--- TEST: applyTax (10% tax on $50) ---");
        double result = service.applyTax(50.0, 10.0);
        System.out.println("  Input   : price=50.0, taxRate=10%");
        System.out.println("  Result  : " + result);
        assertEquals("$50 + 10% tax should be $55", 55.0, result, 0.001);
        System.out.println("  PASSED");
    }

    @Test
    public void testApplyTax_zeroRate() {
        System.out.println("\n--- TEST: applyTax (0% tax – price unchanged) ---");
        double result = service.applyTax(75.0, 0.0);
        System.out.println("  Input   : price=75.0, taxRate=0%");
        System.out.println("  Result  : " + result);
        assertEquals("0% tax should leave price unchanged", 75.0, result, 0.001);
        System.out.println("  PASSED");
    }

    @Test
    public void testApplyTax_negativeTaxRate() {
        System.out.println("\n--- TEST: applyTax (negative tax rate – returns original price) ---");
        double result = service.applyTax(100.0, -5.0);
        System.out.println("  Input   : price=100.0, taxRate=-5% (invalid)");
        System.out.println("  Result  : " + result);
        assertEquals("Negative tax rate should return original price", 100.0, result, 0.001);
        System.out.println("  PASSED");
    }

    // ==================================================================
    // 15. generateInvoice
    // ==================================================================

    @Test
    public void testGenerateInvoice_validOrder() {
        System.out.println("\n--- TEST: generateInvoice (valid order produces invoice) ---");
        service.addProduct(1, "Monitor", 299.99, 5);
        Order order = service.placeOrder(1, "Heidi", 1);
        assertNotNull("Prerequisite: order must be placed", order);

        String invoice = service.generateInvoice(order.getOrderId());
        System.out.println("  Generated invoice:\n" + invoice);
        assertNotNull("Invoice must not be null", invoice);
        assertTrue("Invoice should contain the order id",
                invoice.contains(String.valueOf(order.getOrderId())));
        assertTrue("Invoice should contain the customer name",
                invoice.contains("Heidi"));
        assertTrue("Invoice should contain the product name",
                invoice.contains("Monitor"));
        System.out.println("  PASSED");
    }

    @Test
    public void testGenerateInvoice_cancelledOrder() {
        System.out.println("\n--- TEST: generateInvoice (cancelled order still generates invoice) ---");
        service.addProduct(2, "Keyboard", 49.99, 10);
        Order order = service.placeOrder(2, "Ivan", 1);
        service.cancelOrder(order.getOrderId());

        String invoice = service.generateInvoice(order.getOrderId());
        System.out.println("  Generated invoice for cancelled order:\n" + invoice);
        assertNotNull("Invoice must not be null even for cancelled orders", invoice);
        assertTrue("Invoice should reflect CANCELLED status",
                invoice.contains("CANCELLED"));
        System.out.println("  PASSED");
    }

    @Test
    public void testGenerateInvoice_nonExistentOrder() {
        System.out.println("\n--- TEST: generateInvoice (non-existent order returns error message) ---");
        String invoice = service.generateInvoice(55555);
        System.out.println("  orderId=55555 (never placed)");
        System.out.println("  Result  : " + invoice);
        assertNotNull("Result must not be null for unknown order", invoice);
        assertTrue("Should contain ERROR indicator", invoice.startsWith("ERROR"));
        System.out.println("  PASSED");
    }
}

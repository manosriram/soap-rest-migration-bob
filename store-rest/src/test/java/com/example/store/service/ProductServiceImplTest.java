package com.example.store.service;

import com.example.store.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verbose unit tests for all product-related operations.
 * Direct port of the JUnit 4 StoreServiceImplTest – now JUnit 5.
 */
class ProductServiceImplTest {

    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl();
        System.out.println("\n========================================");
        System.out.println("  [setUp] Fresh ProductServiceImpl created.");
        System.out.println("========================================");
    }

    // ==================================================================
    // addProduct
    // ==================================================================

    @Test
    void addProduct_success() {
        System.out.println("\n--- TEST: addProduct (happy path) ---");
        boolean added = service.addProduct(1, "Laptop", 999.99, 10);
        System.out.println("  Adding id=1, name=Laptop, price=999.99, qty=10");
        System.out.println("  Result  : " + added);
        assertTrue(added, "addProduct should return true for a valid new product");
        System.out.println("  PASSED");
    }

    @Test
    void addProduct_duplicateIdRejected() {
        System.out.println("\n--- TEST: addProduct (duplicate id rejected) ---");
        service.addProduct(1, "Laptop", 999.99, 10);
        boolean addedAgain = service.addProduct(1, "Laptop Clone", 899.99, 5);
        System.out.println("  Trying to add duplicate id=1");
        System.out.println("  Result  : " + addedAgain);
        assertFalse(addedAgain, "addProduct should return false when the id already exists");
        System.out.println("  PASSED");
    }

    @Test
    void addProduct_negativePrice_rejected() {
        System.out.println("\n--- TEST: addProduct (negative price rejected) ---");
        boolean added = service.addProduct(2, "Ghost Product", -5.0, 1);
        System.out.println("  Adding id=2, price=-5.0");
        System.out.println("  Result  : " + added);
        assertFalse(added, "addProduct should reject negative price");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // getProduct
    // ==================================================================

    @Test
    void getProduct_existingProduct() {
        System.out.println("\n--- TEST: getProduct (existing product) ---");
        service.addProduct(10, "Keyboard", 49.99, 50);
        Product p = service.getProduct(10);
        System.out.println("  Fetching id=10");
        System.out.println("  Result  : " + p);
        assertNotNull(p, "getProduct should return a Product for an existing id");
        assertEquals(10, p.id(), "Product id should match");
        assertEquals("Keyboard", p.name(), "Product name should match");
        assertEquals(49.99, p.price(), 0.001, "Product price should match");
        assertEquals(50, p.quantity(), "Product quantity should match");
        System.out.println("  PASSED");
    }

    @Test
    void getProduct_nonExistent_returnsNull() {
        System.out.println("\n--- TEST: getProduct (non-existent id returns null) ---");
        Product p = service.getProduct(9999);
        System.out.println("  Fetching id=9999 (not added)");
        System.out.println("  Result  : " + p);
        assertNull(p, "getProduct should return null when the product does not exist");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // listProducts
    // ==================================================================

    @Test
    void listProducts_empty() {
        System.out.println("\n--- TEST: listProducts (empty catalogue) ---");
        List<Product> list = service.listProducts();
        System.out.println("  No products added yet");
        System.out.println("  Result size : " + list.size());
        assertNotNull(list, "listProducts must return a non-null list");
        assertTrue(list.isEmpty(), "Catalogue should be empty initially");
        System.out.println("  PASSED");
    }

    @Test
    void listProducts_afterAddingMultiple() {
        System.out.println("\n--- TEST: listProducts (after adding 3 products) ---");
        service.addProduct(1, "Mouse",    25.0, 100);
        service.addProduct(2, "Monitor", 300.0,  20);
        service.addProduct(3, "Desk",    150.0,   5);
        List<Product> list = service.listProducts();
        System.out.println("  Products in list: " + list.size());
        list.forEach(pr -> System.out.println("    -> " + pr));
        assertEquals(3, list.size(), "Should return 3 products");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // updateProductPrice
    // ==================================================================

    @Test
    void updateProductPrice_success() {
        System.out.println("\n--- TEST: updateProductPrice (happy path) ---");
        service.addProduct(5, "USB Hub", 19.99, 30);
        boolean updated = service.updateProductPrice(5, 24.99);
        System.out.println("  Updating id=5: old=19.99, new=24.99");
        System.out.println("  Result  : " + updated);
        assertTrue(updated, "updateProductPrice should succeed for existing id");
        assertEquals(24.99, service.getProduct(5).price(), 0.001, "Price should now be 24.99");
        System.out.println("  PASSED");
    }

    @Test
    void updateProductPrice_nonExistentProduct() {
        System.out.println("\n--- TEST: updateProductPrice (non-existent product returns false) ---");
        boolean updated = service.updateProductPrice(999, 50.0);
        System.out.println("  Trying to update id=999 (not added)");
        System.out.println("  Result  : " + updated);
        assertFalse(updated, "updateProductPrice should return false for missing id");
        System.out.println("  PASSED");
    }

    @Test
    void updateProductPrice_negativePrice() {
        System.out.println("\n--- TEST: updateProductPrice (negative new price rejected) ---");
        service.addProduct(5, "USB Hub", 19.99, 30);
        boolean updated = service.updateProductPrice(5, -1.0);
        System.out.println("  Trying to set price=-1.0 for id=5");
        System.out.println("  Result  : " + updated);
        assertFalse(updated, "updateProductPrice should reject a negative new price");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // deleteProduct
    // ==================================================================

    @Test
    void deleteProduct_success() {
        System.out.println("\n--- TEST: deleteProduct (happy path) ---");
        service.addProduct(7, "Webcam", 59.99, 15);
        boolean deleted = service.deleteProduct(7);
        System.out.println("  Deleting id=7");
        System.out.println("  Result  : " + deleted);
        assertTrue(deleted, "deleteProduct should return true for existing id");
        assertNull(service.getProduct(7), "Product should be gone after deletion");
        assertEquals(0, service.listProducts().size(), "Catalogue should be empty after deletion");
        System.out.println("  PASSED");
    }

    @Test
    void deleteProduct_nonExistent() {
        System.out.println("\n--- TEST: deleteProduct (non-existent product returns false) ---");
        boolean deleted = service.deleteProduct(42);
        System.out.println("  Deleting id=42 (not added)");
        System.out.println("  Result  : " + deleted);
        assertFalse(deleted, "deleteProduct should return false for missing id");
        System.out.println("  PASSED");
    }

    // ==================================================================
    // checkStock
    // ==================================================================

    @Test
    void checkStock_sufficient() {
        System.out.println("\n--- TEST: checkStock (sufficient stock) ---");
        service.addProduct(1, "Chair", 89.0, 20);
        boolean inStock = service.checkStock(1, 5);
        System.out.println("  Stock=20, requested=5");
        System.out.println("  Result  : " + inStock);
        assertTrue(inStock, "checkStock should return true when stock >= requested");
        System.out.println("  PASSED");
    }

    @Test
    void checkStock_insufficient() {
        System.out.println("\n--- TEST: checkStock (insufficient stock) ---");
        service.addProduct(1, "Chair", 89.0, 2);
        boolean inStock = service.checkStock(1, 10);
        System.out.println("  Stock=2, requested=10");
        System.out.println("  Result  : " + inStock);
        assertFalse(inStock, "checkStock should return false when stock < requested");
        System.out.println("  PASSED");
    }

    @Test
    void checkStock_productNotFound() {
        System.out.println("\n--- TEST: checkStock (product not found returns false) ---");
        boolean inStock = service.checkStock(999, 1);
        System.out.println("  Product id=999 not in catalogue");
        System.out.println("  Result  : " + inStock);
        assertFalse(inStock, "checkStock should return false if the product is unknown");
        System.out.println("  PASSED");
    }
}

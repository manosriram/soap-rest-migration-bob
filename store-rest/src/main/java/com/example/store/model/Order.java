package com.example.store.model;

/**
 * Immutable order domain model.
 * Replaces the JAXB-annotated POJO from the legacy SOAP service.
 */
public record Order(int orderId, int productId, String customerName, double amount, String status) {

    /** Returns a copy with an updated status. */
    public Order withStatus(String newStatus) {
        return new Order(orderId, productId, customerName, amount, newStatus);
    }
}

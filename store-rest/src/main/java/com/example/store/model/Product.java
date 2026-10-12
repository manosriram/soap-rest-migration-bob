package com.example.store.model;

/**
 * Immutable product domain model.
 * Replaces the JAXB-annotated POJO from the legacy SOAP service.
 */
public record Product(int id, String name, double price, int quantity) {

    /** Returns a copy with an updated price. */
    public Product withPrice(double newPrice) {
        return new Product(id, name, newPrice, quantity);
    }

    /** Returns a copy with an updated quantity. */
    public Product withQuantity(int newQuantity) {
        return new Product(id, name, price, newQuantity);
    }
}

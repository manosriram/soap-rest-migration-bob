package com.example.store.service;

import com.example.store.model.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(int productId, String customerName, int quantity);
    Order getOrder(int orderId);
    List<Order> listOrders();
    boolean cancelOrder(int orderId);
    String generateInvoice(int orderId);
    double calculateDiscount(double originalPrice, double discountPercent);
    double convertCurrency(double amountUSD, double rate);
    double applyTax(double price, double taxRate);
    String sayHello(String name);
}

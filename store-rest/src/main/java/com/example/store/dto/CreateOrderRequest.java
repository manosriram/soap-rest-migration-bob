package com.example.store.dto;

public record CreateOrderRequest(int productId, String customerName, int quantity) {}

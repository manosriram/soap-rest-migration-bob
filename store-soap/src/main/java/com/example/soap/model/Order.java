package com.example.soap.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Simple order model used across several service operations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order", propOrder = {"orderId", "productId", "customerName", "amount", "status"})
public class Order {

    private int orderId;
    private int productId;
    private String customerName;
    private double amount;
    private String status;

    public Order() {}

    public Order(int orderId, int productId, String customerName, double amount, String status) {
        this.orderId = orderId;
        this.productId = productId;
        this.customerName = customerName;
        this.amount = amount;
        this.status = status;
    }

    public int getOrderId()                    { return orderId; }
    public void setOrderId(int orderId)        { this.orderId = orderId; }

    public int getProductId()                  { return productId; }
    public void setProductId(int productId)    { this.productId = productId; }

    public String getCustomerName()                       { return customerName; }
    public void setCustomerName(String customerName)      { this.customerName = customerName; }

    public double getAmount()              { return amount; }
    public void setAmount(double amount)   { this.amount = amount; }

    public String getStatus()              { return status; }
    public void setStatus(String status)   { this.status = status; }

    @Override
    public String toString() {
        return "Order{orderId=" + orderId +
               ", productId=" + productId +
               ", customerName='" + customerName + '\'' +
               ", amount=" + amount +
               ", status='" + status + '\'' +
               '}';
    }
}

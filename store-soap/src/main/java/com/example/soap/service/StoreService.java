package com.example.soap.service;

import com.example.soap.model.Order;
import com.example.soap.model.Product;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Service Endpoint Interface (SEI) for the Store SOAP service.
 *
 * Operations:
 *  1.  sayHello             – simple greeting
 *  2.  addProduct           – add a new product to the catalogue
 *  3.  getProduct           – fetch a product by id
 *  4.  listProducts         – list all products
 *  5.  updateProductPrice   – change the price of a product
 *  6.  deleteProduct        – remove a product
 *  7.  placeOrder           – create an order for a product
 *  8.  getOrder             – fetch an order by id
 *  9.  listOrders           – list all orders
 * 10.  cancelOrder          – cancel an order
 * 11.  calculateDiscount    – calculate a discount for a given price and percentage
 * 12.  convertCurrency      – simple currency conversion (USD → EUR)
 * 13.  checkStock           – return whether a product has sufficient stock
 * 14.  applyTax             – apply a tax rate to a price
 * 15.  generateInvoice      – generate a human-readable invoice string for an order
 */
@WebService(name = "StoreService", targetNamespace = "http://example.com/soap/store")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface StoreService {

    // ------------------------------------------------------------------ 1
    @WebMethod(operationName = "sayHello")
    @WebResult(name = "greeting")
    String sayHello(@WebParam(name = "name") String name);

    // ------------------------------------------------------------------ 2
    @WebMethod(operationName = "addProduct")
    @WebResult(name = "success")
    boolean addProduct(
            @WebParam(name = "id")       int    id,
            @WebParam(name = "name")     String name,
            @WebParam(name = "price")    double price,
            @WebParam(name = "quantity") int    quantity);

    // ------------------------------------------------------------------ 3
    @WebMethod(operationName = "getProduct")
    @WebResult(name = "product")
    Product getProduct(@WebParam(name = "id") int id);

    // ------------------------------------------------------------------ 4
    @WebMethod(operationName = "listProducts")
    @WebResult(name = "products")
    List<Product> listProducts();

    // ------------------------------------------------------------------ 5
    @WebMethod(operationName = "updateProductPrice")
    @WebResult(name = "updated")
    boolean updateProductPrice(
            @WebParam(name = "id")       int    id,
            @WebParam(name = "newPrice") double newPrice);

    // ------------------------------------------------------------------ 6
    @WebMethod(operationName = "deleteProduct")
    @WebResult(name = "deleted")
    boolean deleteProduct(@WebParam(name = "id") int id);

    // ------------------------------------------------------------------ 7
    @WebMethod(operationName = "placeOrder")
    @WebResult(name = "order")
    Order placeOrder(
            @WebParam(name = "productId")    int    productId,
            @WebParam(name = "customerName") String customerName,
            @WebParam(name = "quantity")     int    quantity);

    // ------------------------------------------------------------------ 8
    @WebMethod(operationName = "getOrder")
    @WebResult(name = "order")
    Order getOrder(@WebParam(name = "orderId") int orderId);

    // ------------------------------------------------------------------ 9
    @WebMethod(operationName = "listOrders")
    @WebResult(name = "orders")
    List<Order> listOrders();

    // ----------------------------------------------------------------- 10
    @WebMethod(operationName = "cancelOrder")
    @WebResult(name = "cancelled")
    boolean cancelOrder(@WebParam(name = "orderId") int orderId);

    // ----------------------------------------------------------------- 11
    @WebMethod(operationName = "calculateDiscount")
    @WebResult(name = "discountedPrice")
    double calculateDiscount(
            @WebParam(name = "originalPrice")    double originalPrice,
            @WebParam(name = "discountPercent")  double discountPercent);

    // ----------------------------------------------------------------- 12
    @WebMethod(operationName = "convertCurrency")
    @WebResult(name = "convertedAmount")
    double convertCurrency(
            @WebParam(name = "amountUSD") double amountUSD,
            @WebParam(name = "rate")      double rate);

    // ----------------------------------------------------------------- 13
    @WebMethod(operationName = "checkStock")
    @WebResult(name = "inStock")
    boolean checkStock(
            @WebParam(name = "productId")  int productId,
            @WebParam(name = "requested")  int requested);

    // ----------------------------------------------------------------- 14
    @WebMethod(operationName = "applyTax")
    @WebResult(name = "priceWithTax")
    double applyTax(
            @WebParam(name = "price")   double price,
            @WebParam(name = "taxRate") double taxRate);

    // ----------------------------------------------------------------- 15
    @WebMethod(operationName = "generateInvoice")
    @WebResult(name = "invoice")
    String generateInvoice(@WebParam(name = "orderId") int orderId);
}

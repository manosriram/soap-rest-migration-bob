package com.example.store.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc integration tests for OrderController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    /** Helper: add a product with id=1, Laptop, $999.99, qty=10. */
    private void addLaptop() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Laptop","price":999.99,"quantity":10}
                                """))
                .andExpect(status().isCreated());
    }

    // ------------------------------------------------------------------
    // POST /api/v1/orders
    // ------------------------------------------------------------------

    @Test
    void placeOrder_success_returns201WithOrder() throws Exception {
        addLaptop();
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":1,"customerName":"Bob Smith","quantity":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.customerName").value("Bob Smith"))
                .andExpect(jsonPath("$.amount").value(closeTo(1999.98, 0.01)))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(header().string("Location", containsString("/api/v1/orders/")));
    }

    @Test
    void placeOrder_insufficientStock_returns422() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":2,"name":"Tablet","price":399.99,"quantity":3}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":2,"customerName":"Carol","quantity":10}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void placeOrder_productNotFound_returns422() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":999,"customerName":"Dave","quantity":1}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    // ------------------------------------------------------------------
    // GET /api/v1/orders/{orderId}
    // ------------------------------------------------------------------

    @Test
    void getOrder_existingOrder_returns200() throws Exception {
        addLaptop();
        String response = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":1,"customerName":"Eve","quantity":1}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // orderId starts at 1000
        mockMvc.perform(get("/api/v1/orders/1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1000))
                .andExpect(jsonPath("$.customerName").value("Eve"));
    }

    @Test
    void getOrder_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/orders/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ------------------------------------------------------------------
    // GET /api/v1/orders
    // ------------------------------------------------------------------

    @Test
    void listOrders_empty_returns200EmptyArray() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listOrders_afterPlacing_returnsAll() throws Exception {
        addLaptop();
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":2,"name":"Monitor","price":299.99,"quantity":10}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":1,"customerName":"Alice","quantity":1}
                                """))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":2,"customerName":"Bob","quantity":2}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ------------------------------------------------------------------
    // DELETE /api/v1/orders/{orderId}
    // ------------------------------------------------------------------

    @Test
    void cancelOrder_success_returns200() throws Exception {
        addLaptop();
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":1,"customerName":"Frank","quantity":2}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/orders/1000"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/orders/1000"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelOrder_alreadyCancelled_returns409() throws Exception {
        addLaptop();
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":1,"customerName":"Grace","quantity":1}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/orders/1000")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/orders/1000"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void cancelOrder_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/77777"))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------
    // GET /api/v1/orders/{orderId}/invoice
    // ------------------------------------------------------------------

    @Test
    void generateInvoice_validOrder_returnsPlainText() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Monitor","price":299.99,"quantity":5}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":1,"customerName":"Heidi","quantity":1}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders/1000/invoice"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Heidi")))
                .andExpect(content().string(containsString("Monitor")))
                .andExpect(content().string(containsString("1000")));
    }

    @Test
    void generateInvoice_cancelledOrder_containsCancelledStatus() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":2,"name":"Keyboard","price":49.99,"quantity":10}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":2,"customerName":"Ivan","quantity":1}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/orders/1000")).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/orders/1000/invoice"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CANCELLED")));
    }

    @Test
    void generateInvoice_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/orders/55555/invoice"))
                .andExpect(status().isNotFound());
    }
}

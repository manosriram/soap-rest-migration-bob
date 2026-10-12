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
 * MockMvc integration tests for ProductController.
 * Each test gets a fresh application context via @DirtiesContext to ensure
 * in-memory state is isolated between tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    // ------------------------------------------------------------------
    // POST /api/v1/products
    // ------------------------------------------------------------------

    @Test
    void addProduct_returns201() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Laptop","price":999.99,"quantity":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/products/1")));
    }

    @Test
    void addProduct_duplicateId_returns409() throws Exception {
        String body = """
                {"id":1,"name":"Laptop","price":999.99,"quantity":10}
                """;
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void addProduct_negativePrice_returns409() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":2,"name":"Ghost","price":-5.0,"quantity":1}
                                """))
                .andExpect(status().isConflict());
    }

    // ------------------------------------------------------------------
    // GET /api/v1/products/{id}
    // ------------------------------------------------------------------

    @Test
    void getProduct_existingProduct_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":10,"name":"Keyboard","price":49.99,"quantity":50}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/products/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(49.99))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void getProduct_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ------------------------------------------------------------------
    // GET /api/v1/products
    // ------------------------------------------------------------------

    @Test
    void listProducts_empty_returns200EmptyArray() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listProducts_afterAdding_returns200WithAll() throws Exception {
        for (int i = 1; i <= 3; i++) {
            mockMvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"id":%d,"name":"Item%d","price":10.0,"quantity":5}
                                    """.formatted(i, i)))
                    .andExpect(status().isCreated());
        }
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // ------------------------------------------------------------------
    // PATCH /api/v1/products/{id}/price
    // ------------------------------------------------------------------

    @Test
    void updateProductPrice_success_returns200() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":5,"name":"USB Hub","price":19.99,"quantity":30}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/v1/products/5/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newPrice":24.99}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/products/5"))
                .andExpect(jsonPath("$.price").value(24.99));
    }

    @Test
    void updateProductPrice_notFound_returns404() throws Exception {
        mockMvc.perform(patch("/api/v1/products/999/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newPrice":50.0}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProductPrice_negativePrice_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":5,"name":"USB Hub","price":19.99,"quantity":30}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/v1/products/5/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newPrice":-1.0}
                                """))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------
    // DELETE /api/v1/products/{id}
    // ------------------------------------------------------------------

    @Test
    void deleteProduct_success_returns204() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":7,"name":"Webcam","price":59.99,"quantity":15}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/products/7"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/7"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/products/42"))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------
    // GET /api/v1/products/{id}/stock
    // ------------------------------------------------------------------

    @Test
    void checkStock_sufficient_returnsInStockTrue() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Chair","price":89.0,"quantity":20}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/products/1/stock?requested=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inStock").value(true))
                .andExpect(jsonPath("$.available").value(20));
    }

    @Test
    void checkStock_insufficient_returnsInStockFalse() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":1,"name":"Chair","price":89.0,"quantity":2}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/products/1/stock?requested=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inStock").value(false));
    }

    @Test
    void checkStock_productNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/products/999/stock?requested=1"))
                .andExpect(status().isNotFound());
    }
}

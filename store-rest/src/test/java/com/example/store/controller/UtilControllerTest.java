package com.example.store.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc integration tests for UtilController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UtilControllerTest {

    @Autowired
    MockMvc mockMvc;

    // ------------------------------------------------------------------
    // GET /api/v1/utils/hello
    // ------------------------------------------------------------------

    @Test
    void sayHello_withName_returnsGreeting() throws Exception {
        mockMvc.perform(get("/api/v1/utils/hello?name=Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value(org.hamcrest.Matchers.containsString("Alice")));
    }

    @Test
    void sayHello_noName_returnsFallback() throws Exception {
        mockMvc.perform(get("/api/v1/utils/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("Hello, World!"));
    }

    @Test
    void sayHello_emptyName_returnsFallback() throws Exception {
        mockMvc.perform(get("/api/v1/utils/hello?name=   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("Hello, World!"));
    }

    // ------------------------------------------------------------------
    // GET /api/v1/utils/discount
    // ------------------------------------------------------------------

    @Test
    void calculateDiscount_tenPercent_returns180() throws Exception {
        mockMvc.perform(get("/api/v1/utils/discount?price=200.0&percent=10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountedPrice").value(180.0));
    }

    @Test
    void calculateDiscount_zeroPercent_returnsOriginal() throws Exception {
        mockMvc.perform(get("/api/v1/utils/discount?price=150.0&percent=0.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountedPrice").value(150.0));
    }

    @Test
    void calculateDiscount_outOfRange_returnsOriginal() throws Exception {
        mockMvc.perform(get("/api/v1/utils/discount?price=100.0&percent=110.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountedPrice").value(100.0));
    }

    // ------------------------------------------------------------------
    // GET /api/v1/utils/currency
    // ------------------------------------------------------------------

    @Test
    void convertCurrency_usdToEur_returns92() throws Exception {
        mockMvc.perform(get("/api/v1/utils/currency?amountUSD=100.0&rate=0.92"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(92.0));
    }

    @Test
    void convertCurrency_rateOne_returnsSameAmount() throws Exception {
        mockMvc.perform(get("/api/v1/utils/currency?amountUSD=250.0&rate=1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(250.0));
    }

    @Test
    void convertCurrency_invalidRate_returnsOriginal() throws Exception {
        mockMvc.perform(get("/api/v1/utils/currency?amountUSD=100.0&rate=0.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(100.0));
    }

    // ------------------------------------------------------------------
    // GET /api/v1/utils/tax
    // ------------------------------------------------------------------

    @Test
    void applyTax_tenPercent_returns55() throws Exception {
        mockMvc.perform(get("/api/v1/utils/tax?price=50.0&taxRate=10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceWithTax",
                        org.hamcrest.Matchers.closeTo(55.0, 0.001)));
    }

    @Test
    void applyTax_zeroRate_returnsOriginal() throws Exception {
        mockMvc.perform(get("/api/v1/utils/tax?price=75.0&taxRate=0.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceWithTax").value(75.0));
    }

    @Test
    void applyTax_negativeRate_returnsOriginal() throws Exception {
        mockMvc.perform(get("/api/v1/utils/tax?price=100.0&taxRate=-5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceWithTax").value(100.0));
    }
}

package com.prestek.coltefinanciera.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prestek.coltefinanciera.config.TestSecurityConfig;
import com.prestek.FinancialEntityCore.dto.QuoteDto;
import com.prestek.FinancialEntityCore.request.QuoteRequest;
import com.prestek.FinancialEntityCore.service.AbstractWeightedQuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuoteController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DisplayName("QuoteController Integration Tests")
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AbstractWeightedQuoteService quoteService;

    private QuoteRequest testRequest;
    private QuoteDto testQuote;

    @BeforeEach
    void setUp() {
        testRequest = new QuoteRequest(
                10000000L,    // amount
                36,           // termMonths
                750,          // score
                5000000L,     // monthlyIncome
                1000000L      // monthlyExpenses
        );

        testQuote = new QuoteDto(
                "COLTEFINANCIERA",  // institution
                0.19,               // rateEAmin
                0.25,               // rateEAmax
                330000L,            // monthlyPaymentMin
                380000L,            // monthlyPaymentMax
                48000L,             // feesEstimated
                0.22,               // aprEAEstimated
                "2025-12-31"        // validUntil
        );
    }

    @Test
    @DisplayName("POST /api/quotes - Should generate quote successfully")
    void shouldGenerateQuote() throws Exception {
        // Given
        when(quoteService.quote(any(QuoteRequest.class))).thenReturn(testQuote);

        // When & Then
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.institution").value("COLTEFINANCIERA"))
                .andExpect(jsonPath("$.rateEAmin").value(0.19))
                .andExpect(jsonPath("$.rateEAmax").value(0.25))
                .andExpect(jsonPath("$.monthlyPaymentMin").value(330000L))
                .andExpect(jsonPath("$.monthlyPaymentMax").value(380000L));

        verify(quoteService, times(1)).quote(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("POST /api/quotes - Should handle request with different credit score")
    void shouldHandleRequestWithDifferentCreditScore() throws Exception {
        // Given
        QuoteRequest lowCreditRequest = new QuoteRequest(
                10000000L, 36, 600, 5000000L, 1000000L
        );

        QuoteDto lowCreditQuote = new QuoteDto(
                "COLTEFINANCIERA", 0.26, 0.32, 360000L, 410000L, 55000L, 0.29, "2025-12-31"
        );

        when(quoteService.quote(any(QuoteRequest.class))).thenReturn(lowCreditQuote);

        // When & Then
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowCreditRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aprEAEstimated").value(0.29));

        verify(quoteService, times(1)).quote(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("POST /api/quotes - Should handle request with high DTI")
    void shouldHandleRequestWithHighDTI() throws Exception {
        // Given
        QuoteRequest highDTIRequest = new QuoteRequest(
                10000000L, 36, 750, 5000000L, 2500000L
        );

        when(quoteService.quote(any(QuoteRequest.class))).thenReturn(testQuote);

        // When & Then
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(highDTIRequest)))
                .andExpect(status().isOk());

        verify(quoteService, times(1)).quote(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("POST /api/quotes - Should handle short term loan")
    void shouldHandleShortTermLoan() throws Exception {
        // Given
        QuoteRequest shortTermRequest = new QuoteRequest(
                5000000L, 12, 750, 5000000L, 1000000L
        );

        QuoteDto shortTermQuote = new QuoteDto(
                "COLTEFINANCIERA", 0.17, 0.23, 440000L, 480000L, 35000L, 0.20, "2025-12-31"
        );

        when(quoteService.quote(any(QuoteRequest.class))).thenReturn(shortTermQuote);

        // When & Then
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortTermRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institution").value("COLTEFINANCIERA"));

        verify(quoteService, times(1)).quote(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("POST /api/quotes - Should handle long term loan")
    void shouldHandleLongTermLoan() throws Exception {
        // Given
        QuoteRequest longTermRequest = new QuoteRequest(
                20000000L, 60, 750, 8000000L, 1500000L
        );

        QuoteDto longTermQuote = new QuoteDto(
                "COLTEFINANCIERA", 0.21, 0.27, 430000L, 490000L, 65000L, 0.24, "2025-12-31"
        );

        when(quoteService.quote(any(QuoteRequest.class))).thenReturn(longTermQuote);

        // When & Then
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(longTermRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institution").value("COLTEFINANCIERA"));

        verify(quoteService, times(1)).quote(any(QuoteRequest.class));
    }
}

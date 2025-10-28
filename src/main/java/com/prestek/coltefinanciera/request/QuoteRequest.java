package com.prestek.coltefinanciera.request;

public record QuoteRequest(
        long amount,
        int termMonths,
        int score,
        long monthlyIncome,
        long monthlyExpenses
) { }


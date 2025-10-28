package com.prestek.coltefinanciera.base;

import com.prestek.coltefinanciera.dto.QuoteDto;
import com.prestek.coltefinanciera.request.QuoteRequest;

public interface QuoteProvider {
    String code();                    // Identificador del banco
    QuoteDto quote(QuoteRequest req);// Calcula la cotización
}
package com.prestek.coltefinanciera.service;

import org.springframework.stereotype.Component;

@Component
public class ColtefinancieraQuoteService extends AbstractWeightedQuoteService {
    @Override public String code() { return "COLTEFINANCIERA"; }

    @Override protected double wScore()  { return 0.45; }
    @Override protected double wDTI()    { return 0.30; }
    @Override protected double wTerm()   { return 0.15; }
    @Override protected double wIncome() { return 0.10; }

    @Override protected double kScore()  { return 0.10; }
    @Override protected double kDTI()    { return 0.06; }
    @Override protected double kTerm()   { return 0.03; }
    @Override protected double kIncome() { return 0.04; }

    @Override protected double baseEA()    { return 0.22; }
    @Override protected double floorEA()   { return 0.16; }
    @Override protected double ceilingEA() { return 0.35; }
    @Override protected long   baseFees()  { return 45_000; }
    @Override protected long   minFees()   { return 30_000; }
    @Override protected long   maxFees()   { return 90_000; }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charalav.currencyconverter.model;

/**
 *
 * @author charalav
 */
public interface ConverterDTO {
    
    /**
     *Gets the converted amount
     * @param amount The amount to convert
     * @param fromCurrency The beginning currency
     * @param toCurrency The ending currency
     * @return The converted amount
     */
    double getConvertedAmount(double amount, String fromCurrency, String toCurrency);
    
    /**
     *Gets the euro rate
     * @return The euro rate
     */
    double getRateEuro();
    
    /**
     *Gets the usd rate
     * @return The usd rate
     */
    double getRateUsd();
    
    /**
     *Gets the gbp rate
     * @return The gbp rate
     */
    double getRateGbp();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charalav.currencyconverter.controller;

import com.charalav.currencyconverter.integration.ConverterDAO;
import com.charalav.currencyconverter.model.Converter;
import com.charalav.currencyconverter.model.ConverterDTO;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;


/**
 * A controller. All calls to the model 
 * pass through here.
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ConverterController {
    @EJB ConverterDAO converterDAO;
    
    /**
     *Store the new currency rates to the DB
     * @param rateEuro The euro rate
     * @param rateUsd The usd rate
     * @param rateGbp The gbp rate
     */
    public void storeRates(double rateEuro, double rateUsd, double rateGbp) {
        Converter newConverter = new Converter(rateEuro, rateUsd, rateGbp);
        converterDAO.storeConverter(newConverter);
    }
    
    /**
     *It finds the current ConverterDTO
     * @return ConverterDTO
     */
    public ConverterDTO findConverter(){
        return converterDAO.findConverter();
    }
    
    /**
     *Starts a new currency conversion
     * @param euro The euro rate
     * @param usd The usd rate
     * @param gbp The gbp rate
     * @param amountToConvert The amount that needs to be converted
     * @param fromCur The currency from which to convert
     * @param toCur The currency that corresponds to the desired one
     * @return The converted amount.
     */
    public double startConversion(double euro, double usd, double gbp, double amountToConvert, String fromCur, String toCur) {
        Converter newConverter = new Converter(euro, usd, gbp);
        return newConverter.getConvertedAmount(amountToConvert, fromCur, toCur);
    }
}


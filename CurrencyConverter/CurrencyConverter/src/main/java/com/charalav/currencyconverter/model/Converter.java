/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charalav.currencyconverter.model;

import java.io.Serializable;
import java.util.Random;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author charalav
 */
@Entity
public class Converter implements ConverterDTO, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int converterId;
    private double rateEuro;
    private double rateUsd;
    private double rateGbp;
    
    /**
     * Creates a new instance of Converter
     */
    public Converter() {
    }

    /**
     * Creates a new instance of Converter
     * @param coverterId The primary key of the table
     * @param euro The euro rate
     * @param usd The usd rate
     * @param gbp The gbp rate
     */
    public Converter(double euro, double usd, double gbp) {
        this.converterId = new Random().nextInt(1000);
        this.rateEuro = euro;
        this.rateUsd = usd;
        this.rateGbp = gbp;
    }
    
    /**
     *The algorithm to find the correct conversion
     * @param amount The total amount to convert
     * @param fromCurrency The starting currency
     * @param toCurrency The desired currency
     * @return The converted amount
     */
    @Override
    public double getConvertedAmount(double amount, String fromCurrency, String toCurrency) {
       double rateFrom;
        double rateTo;
        switch(fromCurrency){
            case "euro":
                rateFrom = rateEuro;
                break;
            case "usd":
                rateFrom = rateUsd;
                break;
            case "gbp":
                rateFrom = rateGbp;
                break;
            default:
                rateFrom = 1;
                break;
        }
        switch(toCurrency){
            case "euro":
                rateTo = rateEuro;
                break;
            case "usd":
                rateTo = rateUsd;
                break;
            case "gbp":
                rateTo = rateGbp;
                break;
            default:
                rateTo = 1;
                break;
        }
        return (rateTo/rateFrom)*amount;
    }

    @Override
    public double getRateEuro() {
        return rateEuro;
    }

    @Override
    public double getRateUsd() {
        return rateUsd;
    }

    @Override
    public double getRateGbp() {
        return rateGbp;
    }
    
}

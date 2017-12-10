/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charalav.currencyconverter.view;

import com.charalav.currencyconverter.controller.ConverterController;
import com.charalav.currencyconverter.model.ConverterDTO;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author charalav
 */
@Named("converterManager")
@ConversationScoped
public class ConverterManager implements Serializable {
    @EJB
    private ConverterController converterController;
    private ConverterDTO currentConverter;
    private double rateEuro;
    private double rateUsd;
    private double rateGbp;
    private double amountToConvert;
    private String fromCur;
    private String toCur;
    private Double convertedAmount;
    private Exception transactionFailure;
    @Inject
    private Conversation conversation;
    
    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    private void handleException(Exception e) {
        stopConversation();
        e.printStackTrace(System.err);
        transactionFailure = e;
    }
    
     /**
     * @return <code>true</code> if the latest transaction succeeded, otherwise
     * <code>false</code>.
     */
    public boolean getSuccess() {
        return transactionFailure == null;
    }

    /**
     * Returns the latest thrown exception.
     * @return transactionFailure.
     */
    public Exception getException() {
        return transactionFailure;
    }
    
    /**
     *Stores new currency rates to the server's DB.
     */
    public void storeRates(){
        try {
            startConversation();
            transactionFailure = null;
            converterController.storeRates(rateEuro, rateUsd, rateGbp);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    /**
     *Calculates the conversion between currencies.
     */
    public void startConversion(){
        try {
            startConversation();
            transactionFailure = null;
            currentConverter = converterController.findConverter();
            convertedAmount = converterController.startConversion(currentConverter.getRateEuro(), 
                    currentConverter.getRateUsd(), currentConverter.getRateGbp(),
                    amountToConvert, fromCur, toCur);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    public void setRateEuro(double rateEuro) {
        if (rateEuro <= 0) {
            
        }
        this.rateEuro = rateEuro;
    }
    
    public double getRateEuro(){
        return rateEuro;
    }
    
    public void setRateUsd(double rateUsd){
        this.rateUsd = rateUsd;
    }
    
    public double getRateUsd(){
        return rateUsd;
    }
    
    public void setRateGbp(double rateGbp){
        this.rateGbp = rateGbp;
    }
    
    public double getRateGbp(){
        return rateGbp;
    }
    
    public void setAmountToConvert(double amountToConvert){
        this.amountToConvert = amountToConvert;
    }
    
    public double getAmountToConvert(){
        return amountToConvert;
    }
    
    public void setFromCur(String fromCur){
        this.fromCur = fromCur;
    }
    
    public String getFromCur(){
        return fromCur;
    }
    
    public void setToCur(String toCur){
        this.toCur = toCur;
    }
    
    public String getToCur(){
        return toCur;
    }
    
    public void setConvertedAmount(Double convertedAmount){
        this.convertedAmount = convertedAmount;
    }
    
    public Double getConvertedAmount(){
        return convertedAmount;
    }
}

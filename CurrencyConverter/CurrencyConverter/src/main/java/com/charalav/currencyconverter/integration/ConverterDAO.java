/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charalav.currencyconverter.integration;

import com.charalav.currencyconverter.model.Converter;
import com.charalav.currencyconverter.model.ConverterDTO;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author charalav
 */
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class ConverterDAO {
    @PersistenceContext(unitName = "currencyPU")
    private EntityManager em;
    
    /**
     *Stores a new Converter to DB
     * @param newConverter The instance of a Converter
     */
    public void storeConverter(Converter newConverter) {
        em.persist(newConverter);
        
    }
    
    /**
     *Finds all the Converters in the DB, and picks the last registered
     * @return The last Converter in the DB
     */
    public ConverterDTO findConverter() {
        List result = em.createQuery("SELECT c FROM Converter c").getResultList();
        Converter converter = (Converter) result.get(result.size() - 1);
        if (converter == null) {
            throw new EntityNotFoundException("No currencies registered!");
        }
        return converter;
    }    
}

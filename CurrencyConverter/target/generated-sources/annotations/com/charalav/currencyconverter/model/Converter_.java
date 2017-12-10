package com.charalav.currencyconverter.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-05T18:51:22")
@StaticMetamodel(Converter.class)
public class Converter_ { 

    public static volatile SingularAttribute<Converter, Integer> converterId;
    public static volatile SingularAttribute<Converter, Double> rateUsd;
    public static volatile SingularAttribute<Converter, Double> rateEuro;
    public static volatile SingularAttribute<Converter, Double> rateGbp;

}
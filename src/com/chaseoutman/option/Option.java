package com.chaseoutman.option;
public class Option {
    private double strikePrice;
    private double optionPrice;
    private String impliedVolatility;
    private double delta;

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public double getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(double optionPrice) {
        this.optionPrice = optionPrice;
    }

    public String getImpliedVolatility() {
        return impliedVolatility;
    }

    public void setImpliedVolatility(String impliedVolatility) {
        this.impliedVolatility = impliedVolatility;
    }
}
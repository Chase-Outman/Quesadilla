package com.chaseoutman.stock;

import java.util.ArrayList;
public class Stock {
    private double price;
    private String ticker;
    private final ArrayList<Long> optionDates;

    public Stock() {
        this.optionDates = new ArrayList<>();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public ArrayList<Long> getOptionDates() {
        return optionDates;
    }

    public void addOptionDate(long optionDate) {
        this.optionDates.add(optionDate);
    }
}

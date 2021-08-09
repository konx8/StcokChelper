package com.example.stockhelper;

public class SharesBought {
    String stockName;
    String stockPrice;
    int stockNumbers;

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public int getStockNumbers() {
        return stockNumbers;
    }

    public void setStockNumbers(int stockNumbers) {
        this.stockNumbers = stockNumbers;
    }

    public SharesBought(String sName, String sPrice, int sNumber){

        this.stockName = sName;
        this.stockPrice = sPrice;
        this.stockNumbers = sNumber;

    }
    public SharesBought(){}
}



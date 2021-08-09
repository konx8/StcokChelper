package com.example.stockhelper;

import android.os.Parcel;
import android.os.Parcelable;

public class Stock implements Parcelable {

    String symbol;
    String name;
    String price;
    String open;
    String high;
    String low;
    String volume;
    String lastTradingDay;
    String pervClose;
    String change;
    String changePercentage;

    public Stock(String aSymbol, String aName, String aOpen, String aHigh
            , String aLow, String aPrice, String aVolume, String aLastTradingDay, String aPervClose, String aChange
            , String aChangePercentage){
        this.symbol = aSymbol;
        this.name = aName;
        this.price = aPrice;
        this.open = aOpen;
        this.high = aHigh;
        this.low = aLow;
        this.volume = aVolume;
        this.lastTradingDay = aLastTradingDay;
        this.pervClose = aPervClose;
        this.change = aChange;
        this.changePercentage = aChangePercentage;
    }


    protected Stock(Parcel in) {
        symbol = in.readString();
        name = in.readString();
        price = in.readString();
        open = in.readString();
        high = in.readString();
        low = in.readString();
        volume = in.readString();
        lastTradingDay = in.readString();
        pervClose = in.readString();
        change = in.readString();
        changePercentage = in.readString();
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(symbol);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(open);
        dest.writeString(high);
        dest.writeString(low);
        dest.writeString(volume);
        dest.writeString(lastTradingDay);
        dest.writeString(pervClose);
        dest.writeString(change);
        dest.writeString(changePercentage);
    }
}

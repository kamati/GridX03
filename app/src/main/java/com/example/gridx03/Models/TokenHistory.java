package com.example.gridx03.Models;

public class TokenHistory {
    private long ID;
    String mTokeID;
    String mDate;
    String mAmount;

    void TokenHistory(String tokenID,String date,String amount){

        this.mTokeID = tokenID;
        this.mDate=date;
        this.mAmount= amount;
    }
    public long getID() {
        return ID;
    }
    public String getmTokeID() {
        return mTokeID;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmAmount() {
        return mAmount;
    }
    public void setID(long ID) {
        this.ID = ID;
    }
    public void setmTokeID(String mTokeID) {
        this.mTokeID = mTokeID;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }
}

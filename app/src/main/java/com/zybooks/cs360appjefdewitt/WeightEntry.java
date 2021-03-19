package com.zybooks.cs360appjefdewitt;

public class WeightEntry {
    private String mAccount;
    private String mDate;
    private String mWeight;
    private boolean mGoal;

    public WeightEntry () {}

    public WeightEntry(String account, String date, String weight, boolean goal) {
        mAccount = account;
        mDate = date;
        mWeight = weight;
        mGoal = goal;
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        this.mAccount = account;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String weight) {
        this.mWeight = weight;
    }

    public boolean isGoal() {
        return mGoal;
    }

    public void setIsGoal(boolean mGoal) {
        this.mGoal = mGoal;
    }
}

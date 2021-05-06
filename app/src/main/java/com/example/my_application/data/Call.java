package com.example.my_application.data;

import java.util.Date;

public class Call implements Comparable<Call> {

    private final String num;
    private final Date date;
    private final int duration;
    private int callsNum;
    private final String cachedName;

    public Call(String num, Date date, int duration, int callsNum, String cachedName){
        this.num = num;
        this.date = date;
        this.duration = duration;
        this.callsNum = callsNum;
        this.cachedName = cachedName;
    }

    public String getNum() {
        return num;
    }

    public Date getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }

    public int getCallsNum() {
        return callsNum;
    }

    public String getCachedName() {
        return cachedName;
    }

    public void setCallsNum(int callsNum) {
        this.callsNum = callsNum;
    }

    @Override
    public int compareTo(Call call) {
       switch (getDate().compareTo(call.getDate())){
           case -1:
               return 1;
           case 1:
               return -1;
           default:
               return 0;
       }
    }
}

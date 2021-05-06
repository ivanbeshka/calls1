package com.example.my_application;

public class Log {
    private final static String LOG_CODE ="debug_action";

    public static void debug(String s){
        android.util.Log.d(LOG_CODE,s);
    }
}
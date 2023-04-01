package com.example.shopuserservice.web.util;

public class LogTrace {

    public static void logWithThread(String str){
        System.out.println("[thread:"+Thread.currentThread().getName()+"] "+str);
    }
}

package com.netty.current.client.mertic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MerticManager {

    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static AtomicInteger atomicInteger1 = new AtomicInteger(0);

    public static ConcurrentHashMap<String,Long> time = new ConcurrentHashMap<>();

    public static Long startTime;


    public static int getTcpCount() {
        return atomicInteger.getAndIncrement();
    }
    public static int getTcpCount0() {
        return atomicInteger1.getAndIncrement();
    }


    public static void main(String[] args) {
        for (int i=0;i<1000;i++) {
            System.out.println(getTcpCount());
        }
    }


}

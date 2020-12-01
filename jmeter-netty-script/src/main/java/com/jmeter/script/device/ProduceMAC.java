package com.jmeter.script.device;

import com.google.common.io.Files;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.Jedis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class ProduceMAC {

    private static final ConcurrentLinkedQueue<Jedis> JEDISQUEUE = new ConcurrentLinkedQueue<Jedis>();

    private static BufferedWriter WRITER = null;
    private static AtomicInteger MAC_NUM = new AtomicInteger(0);

    static {
        // init JEDISQUEUE
        for (int i=0;i<1000;i++) {
            Jedis redis = new Jedis("127.0.0.1", 6379);
            redis.select(1);
            JEDISQUEUE.add(redis);
        }

    }

    public static void main(String[] args) {



        HashSet<String> MacSet = new HashSet<String>();
        String linChar = ":";

        for (int i=0;i<1000000;i++) {

            String mac_1 = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 2);
            String mac_2 = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(2, 4);
            String mac_3 = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(4, 6);
            String mac_4 = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(6, 8);
            String mac_5 = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(8, 10);
            String mac_6 = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(10, 12);

            StringBuilder mac = new StringBuilder();
            mac.append(mac_1).append(linChar).append(mac_2).append(linChar).append(mac_3)
                    .append(linChar).append(mac_4).append(linChar).append(mac_5).append(linChar)
                    .append(mac_6);

            MacSet.add(mac.toString());

        }


        if (1000000 == MacSet.size()) {
            PersistenceMAC(MacSet);
        }

    }


    private static void PersistenceMAC (HashSet<String> macSet) {
        System.out.println("> redis");
        ExecutorService es = Executors.newFixedThreadPool(100);
        for (String mac : macSet) {
            es.execute(new Runnable() {

                @Override
                public void run() {
                    Jedis redis;
                    try {
                        redis = JEDISQUEUE.poll();
                        int c = ProduceMAC.MAC_NUM.incrementAndGet();
                        redis.hset("dev_mac",String.valueOf(c), mac);
                        JEDISQUEUE.add(redis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }




}

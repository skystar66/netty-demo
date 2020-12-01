package com.netty.current.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.netty.*"})
public class Client{


    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);

    }



}

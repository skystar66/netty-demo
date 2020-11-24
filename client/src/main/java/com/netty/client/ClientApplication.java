package com.netty.client;

import com.netty.client.client.TopicClient;
import com.netty.client.config.TopicClientConfig;
import com.netty.client.pool.ConnectionPoolFactory;
import com.netty.core.client.init.RpcClientInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.netty.*"})
public class ClientApplication implements ApplicationRunner {

    @Autowired
    TopicClientConfig rpcConfig;

    @Autowired
    RpcClientInitializer rpcClientInitializer;


    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread thread = new Thread(new TopicClient(rpcConfig, rpcClientInitializer));
        thread.setDaemon(true);
        thread.start();
    }


}

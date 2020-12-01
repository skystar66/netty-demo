package com.netty.current.server;

import com.netty.core.server.init.RpcServerInitializer;
import com.netty.current.server.config.TopicServerConfig;
import com.netty.current.server.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.netty.*"})
public class ProviderApplication implements ApplicationRunner {


    @Autowired
    TopicServerConfig rpcConfig;

    @Autowired
    RpcServerInitializer rpcServerInitializer;


    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread thread = new Thread(new Server(rpcConfig, rpcServerInitializer));
        thread.setDaemon(true);
        thread.start();
    }
}


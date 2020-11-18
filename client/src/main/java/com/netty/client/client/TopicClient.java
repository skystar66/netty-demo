package com.netty.client.client;

import com.netty.client.config.TopicClientConfig;
import com.netty.client.pool.ConnectionPoolFactory;
import com.netty.core.client.init.RpcClientInitializer;
import com.netty.core.dto.ManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class TopicClient implements Runnable {


    TopicClientConfig rpcConfig;

    RpcClientInitializer rpcClientInitializer;

    ConnectionPoolFactory connectionPoolFactory;


    public TopicClient(TopicClientConfig rpcConfig,
                       RpcClientInitializer rpcClientInitializer
    ,ConnectionPoolFactory connectionPoolFactory) {
        this.rpcConfig = rpcConfig;
        this.rpcClientInitializer = rpcClientInitializer;
        this.connectionPoolFactory=connectionPoolFactory;
    }

    @Override
    public void run() {
        // 2. 初始化RPC Server
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setRpcPort(rpcConfig.getPort());
        managerProperties.setRpcHost(rpcConfig.getHost());


        connectionPoolFactory.init();

//        rpcClientInitializer.init(Arrays.asList(managerProperties), true);
    }
}

package com.netty.current.server.server;


import com.netty.core.dto.ManagerProperties;
import com.netty.core.server.init.RpcServerInitializer;
import com.netty.current.server.config.TopicServerConfig;

public class Server implements Runnable {


    TopicServerConfig rpcConfig;

    RpcServerInitializer rpcServerInitializer;

    public Server(TopicServerConfig rpcCnfig, RpcServerInitializer rpcServerInitialiozer) {


        this.rpcConfig=rpcCnfig;
        this.rpcServerInitializer=rpcServerInitialiozer;

    }


    @Override
    public void run() {

        // 2. 初始化RPC Server
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setCheckTime(rpcConfig.getHeartTime());
        managerProperties.setRpcPort(rpcConfig.getPort());
        managerProperties.setRpcHost(rpcConfig.getHost());
        rpcServerInitializer.init(managerProperties);

    }
}

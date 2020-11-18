package com.netty.server.server;


import com.netty.core.dto.ManagerProperties;
import com.netty.core.server.init.RpcServerInitializer;
import com.netty.server.config.TopicServerConfig;

public class Server implements Runnable {


    TopicServerConfig rpcConfig;

    RpcServerInitializer rpcServerInitializer;

    public Server(TopicServerConfig rpcConfig,
                  RpcServerInitializer rpcServerInitializer) {
        this.rpcConfig = rpcConfig;
        this.rpcServerInitializer = rpcServerInitializer;
    }


    @Override
    public void run() {

//        // 1. 配置
//        if (rpcConfig.getWaitTime() <= 5) {
//            rpcConfig.setWaitTime(1000);
//        }
//        if (rpcConfig.getAttrDelayTime() < 0) {
//            //网络延迟时间 8s
//            rpcConfig.setAttrDelayTime(txManagerConfig.getDtxTime());
//        }

        // 2. 初始化RPC Server
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setCheckTime(rpcConfig.getHeartTime());
        managerProperties.setRpcPort(rpcConfig.getPort());
        managerProperties.setRpcHost(rpcConfig.getHost());
        rpcServerInitializer.init(managerProperties);
    }
}

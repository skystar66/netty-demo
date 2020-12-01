package com.netty.server.server;


import com.netty.core.dto.ManagerProperties;
import com.netty.core.helper.ZkConfigHelper;
import com.netty.core.reg.RegZookeeper;
import com.netty.core.server.init.RpcServerInitializer;
import com.netty.core.utils.Constants;
import com.netty.core.utils.IpUtil;
import com.netty.server.config.TopicServerConfig;

public class Server implements Runnable {


    TopicServerConfig rpcConfig;

    RpcServerInitializer rpcServerInitializer;

    public Server(TopicServerConfig rpcCnfig,
                  RpcServerInitializer rpcServerInitialiozer) {

        this.rpcConfig = rpcCnfig;
        this.rpcServerInitializer = rpcServerInitialiozer;
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
        managerProperties.setCheckTime(ZkConfigHelper.getInstance().getCommonConfig().getHeartTime());
        managerProperties.setRpcPort(Constants.SERVER_PORT);
        managerProperties.setRpcHost(IpUtil.getLocalIP());
        rpcServerInitializer.init(managerProperties);

        /**注册节点*/
        new RegZookeeper().regIp(Constants.SERVER_CLUSTER);

    }
}

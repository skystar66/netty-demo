package com.netty.client.client;

import com.netty.client.config.TopicClientConfig;
import com.netty.client.pool.ConnectionPoolFactory;
import com.netty.client.pool.monitor.ConnectQueueMonitor;
import com.netty.core.client.init.RpcClientInitializer;
import com.netty.core.cluster.ClusterCenter;
import com.netty.core.utils.Constants;
import com.netty.core.vo.ServerInfoVO;

public class TopicClient implements Runnable {


    TopicClientConfig rpcConfig;

    RpcClientInitializer rpcClientInitializer;



    public TopicClient(TopicClientConfig rpcConfig,
                       RpcClientInitializer rpcClientInitializer) {
        this.rpcConfig = rpcConfig;
        this.rpcClientInitializer = rpcClientInitializer;

    }

    @Override
    public void run() {
        ServerInfoVO serverInfoVO = ServerInfoVO.builder()
                .zkServerPath(Constants.SERVER_CLUSTER).build();
        /**初始化连接池*/
        ConnectionPoolFactory.getInstance().zkSyncRpcServer(serverInfoVO);
        /**监听节点变化*/
        ClusterCenter.getInstance().listenerServerRpc();
        /**监听连接池变化*/
        ClusterCenter.getInstance().listenerServerRpcPoolSize();
        /**监控连接池队列*/
        ConnectQueueMonitor.getInstance().start();

    }
}

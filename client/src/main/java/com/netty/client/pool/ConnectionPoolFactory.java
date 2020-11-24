package com.netty.client.pool;

import com.netty.client.pool.client.RpcClient;
import com.netty.client.pool.connect.ConnectionCache;
import com.netty.client.pool.manager.RpcClientManager;
import com.netty.core.client.init.RpcClientInitializer;
import com.netty.core.helper.ZkConfigHelper;
import com.netty.core.mq.MQProvider;
import com.netty.core.utils.Constants;
import com.netty.core.utils.IpUtil;
import com.netty.core.utils.SpringUtil;
import com.netty.core.vo.ServerInfoVO;
import com.netty.zookeeper.ZkHelp;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ConnectionPoolFactory {
    private static class InstanceHolder{
        public static final ConnectionPoolFactory instance = new ConnectionPoolFactory();
    }

    public ConnectionPoolFactory() {
       if (rpcClientInitializer == null) {
           rpcClientInitializer = SpringUtil.getBean(RpcClientInitializer.class);
       }
    }

    public static ConnectionPoolFactory getInstance(){
        return InstanceHolder.instance;
    }

    RpcClientInitializer rpcClientInitializer;


    /**
     * 初始化zk RPC连接
     *
     * @param
     */
    public void zkSyncRpcServer(ServerInfoVO serverInfoVO) {

        int rpcPoolSize = ZkConfigHelper.getInstance().getRpcPoolSize();
        int cacheRpcpoolSize = ConnectionCache.rpcPoolSize();
        int initPoolSize = rpcPoolSize - cacheRpcpoolSize;
        int initIndex = 0;
        if (cacheRpcpoolSize > 0) {
            initIndex = cacheRpcpoolSize;
        }
        ZkHelp zk = ZkHelp.getInstance();
        List<String> rpcServerList = zk.getChildren(serverInfoVO.getZkServerPath());
        final String localIp = IpUtil.getLocalIP();
        log.info("开始注册rpc长连接服务...	localIp={},  rpcServerList={},  zkPath={}", localIp, rpcServerList, serverInfoVO.getZkServerPath());
        for (final String serverIp : rpcServerList) {
            log.info("serverIp:{}", serverIp);
            try {
                //创建连接池
                for (int index = initIndex; index < rpcPoolSize; index++) {
                    int finalIndex = index;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RpcClientManager.getInstance().connect(serverIp,Constants.SERVER_PORT, finalIndex);
                        }
                    }).start();
                }
            } catch (Exception e) {
                log.error("RPC create error! 服务创建失败! host=" + serverIp + "	" + e);
            }
        }
    }


}

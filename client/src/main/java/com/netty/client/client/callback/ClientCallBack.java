package com.netty.client.client.callback;

import com.netty.client.pool.connect.ConnectionCache;
import com.netty.core.callback.CallBack;
import com.netty.core.client.init.RpcClientInitializer;
import com.netty.core.mq.MQProvider;
import com.netty.core.vo.ServerInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClientCallBack implements CallBack {


    @Autowired
    RpcClientInitializer rpcClientInitializer;

    /**执行重连*/
    @Override
    public void retryConnect(String rpcServer, Integer port, Integer index) {
        ServerInfoVO serverInfoVO = ServerInfoVO.builder().rpcServerIndex(index)
                .rpcPort(port)
                .rpcServer(rpcServer).build();
        MQProvider.getRetryConnectQueue().push(serverInfoVO, Duration.ofMillis(2000));
    }
    /**删除连接池连接*/
    @Override
    public void removeConnect(String rpcPoolIndex) {
        ConnectionCache.remove(rpcPoolIndex);
    }
}

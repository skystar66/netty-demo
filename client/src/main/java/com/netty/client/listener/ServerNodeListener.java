package com.netty.client.listener;

import com.netty.client.pool.ConnectionPoolFactory;
import com.netty.core.event.EventListener;
import com.netty.core.utils.Constants;
import com.netty.core.vo.ServerInfoVO;
import org.springframework.stereotype.Component;

@Component
public class ServerNodeListener implements EventListener {

    @Override
    public void serverNodeChange(ServerInfoVO serverInfoVO) {
        ConnectionPoolFactory.getInstance().zkSyncRpcServer(serverInfoVO);
    }


    @Override
    public void rpcPoolChange(int rpcPoolSize) {
        ServerInfoVO serverInfoVO = ServerInfoVO.builder().zkServerPath(Constants.SERVER_CLUSTER)
                .rpcPort(Constants.SERVER_PORT).build();
        ConnectionPoolFactory.getInstance().zkSyncRpcServer(serverInfoVO);
    }
}

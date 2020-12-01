package com.netty.current.server.callback;

import org.springframework.stereotype.Component;

@Component
public class ServerCallBack implements com.netty.core.callback.CallBack {


    @Override
    public void retryConnect(String rpcServer, Integer port, Integer index) {

    }

    @Override
    public void removeConnect(String rpcPoolIndex) {

    }
}

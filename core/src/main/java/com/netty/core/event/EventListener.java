package com.netty.core.event;

import com.netty.core.vo.ServerInfoVO;

public interface EventListener {


    /**
     * zk 服务集群节点变化通知
     */
    public void serverNodeChange(ServerInfoVO serverInfoVO);


    /**
     * zk 连接池变化通知
     */
    public void rpcPoolChange(int rpcPoolSize);

}

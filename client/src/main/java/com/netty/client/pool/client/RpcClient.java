package com.netty.client.pool.client;

import com.netty.core.client.init.RpcClientInitializer;
import com.netty.msg.dto.RpcCmd;
import com.netty.msg.utils.AttributeKeys;
import com.netty.msg.utils.Constants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Rpc 连接登录 客户端
 *
 * @author xl
 * @version 2020年11月20日
 */
public class RpcClient {

    private String rpcServer;
    private Integer rpcPort;
    private int index;
    RpcClientInitializer rpcClientInitializer;
    private Channel channel;


    public RpcClient(String rpcServer, int rpcPort, int index,
                     RpcClientInitializer rpcClientInitializer) {
        this.rpcServer = rpcServer;
        this.rpcPort = rpcPort;
        this.index = index;
        this.rpcClientInitializer = rpcClientInitializer;
        connection();
    }


    public void connection() {
        final ChannelFuture future = rpcClientInitializer.initFuture(rpcServer, rpcPort, true);
        if (future == null) {
            return;
        }
        channel = future.channel();
        /**channel上绑定rpc数据*/
        channel.attr(AttributeKeys.RPC_SERVER).set(rpcServer);
        channel.attr(AttributeKeys.RPC_PORT).set(rpcPort);
        channel.attr(AttributeKeys.RPC_INDEX).set(index);
        channel.attr(AttributeKeys.RPC_POOL_KEY).set(rpcServer + Constants.SEQ + rpcPort + Constants.SEQ + index);
    }

    /**
     * 发送消息
     */
    public void sendMsg(RpcCmd rpcCmd) {
        channel.writeAndFlush(rpcCmd);
    }


}
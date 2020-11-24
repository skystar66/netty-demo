package com.netty.core.client.handler;


import com.netty.core.callback.CallBack;
import com.netty.core.util.SnowflakeIdWorker;
import com.netty.msg.MessageConstants;
import com.netty.msg.dto.MessageDto;
import com.netty.msg.dto.RpcCmd;
import com.netty.msg.manager.SocketChannelManager;
import com.netty.msg.utils.AttributeKeys;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.SocketAddress;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/21
 *
 * @author xuliang
 * @desc:重试handler
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyClientRetryHandler extends ChannelInboundHandlerAdapter {

    private RpcCmd heartCmd;

    @Autowired
    CallBack callBack;

    /**
     * 构建心跳信息
     */
    public NettyClientRetryHandler() {
        MessageDto messageDto = new MessageDto();
        messageDto.setCmd(MessageConstants.ACTION_HEART_CHECK);
        heartCmd = new RpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(MessageConstants.ACTION_HEART_CHECK
                + SnowflakeIdWorker.getInstance().nextId());
        heartCmd.setEvent(MessageConstants.ACTION_HEART_CHECK);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String rpcServer = ctx.channel().attr(AttributeKeys.RPC_SERVER).get();
        Integer rpcPort = ctx.channel().attr(AttributeKeys.RPC_PORT).get();
        Integer rpcIndex = ctx.channel().attr(AttributeKeys.RPC_INDEX).get();

        String localAddress = ctx.channel().localAddress().toString();
        String remoteAddress = ctx.channel().remoteAddress().toString();
        log.info("连接非活动!!!! rpcServer={}, rpcPort={}, channel={}, localAddress={}", rpcServer, rpcPort, ctx.channel(), localAddress);

        closeChannel(ctx);
        //解决IP为0.0.0.0/0.0.0.0:33703的问题
        if(localAddress.startsWith("0.0.0.0") || remoteAddress.startsWith("0.0.0.0")){
            //停止
            log.error("localAddress={} 为无效地址, 停止重连!", localAddress);
        }else{
            log.info("开始执行重连业务...");
            //重连连接
            callBack.retryConnect(rpcServer, rpcPort, rpcIndex);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientRetryHandler - exception . ", cause);
        if (cause instanceof ConnectException) {
            String rpcServer = ctx.channel().attr(AttributeKeys.RPC_SERVER).get();
            Integer rpcPort = ctx.channel().attr(AttributeKeys.RPC_PORT).get();
            Integer rpcIndex = ctx.channel().attr(AttributeKeys.RPC_INDEX).get();
            String rpcPoolKey = ctx.channel().attr(AttributeKeys.RPC_POOL_KEY).get() ;
            Thread.sleep(1000 * 15);
            log.error("try connect tx-manager:{} ", ctx.channel().remoteAddress());
            callBack.removeConnect(rpcPoolKey);
            callBack.retryConnect(rpcServer, rpcPort, rpcIndex);
        }
        //发送数据包检测是否断开连接.
        ctx.writeAndFlush(heartCmd);

    }


    private void closeChannel(ChannelHandlerContext ctx) throws InterruptedException {
        //清除map中连接信息
        String rpcPoolIndex = ctx.channel().attr(AttributeKeys.RPC_POOL_KEY).get() ;
        callBack.removeConnect(rpcPoolIndex);
        log.info("清除rpcPoolIndex={}", rpcPoolIndex);
        ctx.channel().close().sync();
    }

}

package com.netty.client.client;

import com.netty.client.loadbalance.RpcLoadBalance;
import com.netty.core.content.RpcCmdContext;
import com.netty.core.content.RpcContent;
import com.netty.core.util.SnowflakeIdWorker;
import com.netty.msg.dto.MessageDto;
import com.netty.msg.dto.RpcCmd;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: netty rpc  client  通讯实现类
 * Company: wanbaApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@Component
@Slf4j
public class NettyRpcClient extends RpcClient {

    @Autowired
    RpcLoadBalance rpcLoadBalancel;


    @Override
    public MessageDto request(RpcCmd rpcCmd) throws Exception {
        return request(rpcCmd, 2000);
    }

    @Override
    public MessageDto request(RpcCmd rpcCmd, long timeout) throws Exception {
        long startTime = System.currentTimeMillis();
        String key = String.valueOf(SnowflakeIdWorker.getInstance().nextId());
        RpcContent rpcContent = RpcCmdContext.getInstance().addKey(key);
        rpcCmd.setKey(key);
        MessageDto result = request0(rpcContent, rpcCmd, timeout);
        log.info("cmd request used time: {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    private MessageDto request0(RpcContent rpcContent, RpcCmd rpcCmd, long timeout) throws Exception {
        log.info("get channel, key:{}", rpcCmd.getKey());
        Channel channel = rpcLoadBalancel.getRemoteChannel();
        channel.writeAndFlush(rpcCmd);
        log.info("await response key : {}", rpcCmd.getKey());
        //阻塞结果
        if (timeout < 0) {
            //一直阻塞
            rpcContent.await();
        } else {
            rpcContent.await(timeout);
        }
        MessageDto messageDto = rpcContent.getRes();
        return messageDto;
    }


    @Override
    public void request2(RpcCmd rpcCmd) throws Exception {
        Channel channel = rpcLoadBalancel.getRemoteChannel();
        channel.writeAndFlush(rpcCmd);
    }
}

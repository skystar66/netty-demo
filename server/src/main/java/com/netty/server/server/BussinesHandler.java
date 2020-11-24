package com.netty.server.server;

import com.netty.msg.MessageConstants;
import com.netty.msg.RpcAnswer;
import com.netty.msg.dto.RpcCmd;
import com.netty.msg.enums.ResponseCode;
import com.netty.server.mertic.MerticManager;
import com.netty.server.server.channel.NettyChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class BussinesHandler implements RpcAnswer {


    ExecutorService executors = Executors.newFixedThreadPool(100);

    @Override
    public void callback(RpcCmd rpcCmd) {


        executors.execute(new Runnable() {
            @Override
            public void run() {
                //获取channel
                Channel channel = NettyChannelManager.getInstance().getChannel(rpcCmd.getRemoteKey());
                if (channel == null) {
                    log.info("channel 已失效！");
                    return;
                }
                if (Objects.isNull(rpcCmd)) {
                    channel.writeAndFlush(ResponseCode.INVALID_EVENT);
                    log.info("msg is null");
                    return;
                }

                if (MerticManager.getTcpCount0()==0) {

                    log.info("设置 time 时间");
                    MerticManager.time.put("time",System.currentTimeMillis());
                }

                if (MerticManager.getTcpCount()>=2999) {

                    log.info("1000条消息，共耗时：{}ms",(System.currentTimeMillis()-MerticManager.time.get("time")));
                    MerticManager.atomicInteger.set(0);
                    MerticManager.atomicInteger1.set(0);
                    MerticManager.time.remove("time");

                }

                channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, MessageConstants.STATE_OK));

            }
        });




    }
}

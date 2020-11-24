package com.netty.core.client.handler;

import com.netty.core.callback.CallBack;
import com.netty.core.util.SnowflakeIdWorker;
import com.netty.msg.MessageConstants;
import com.netty.msg.dto.MessageDto;
import com.netty.msg.dto.RpcCmd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class KeepaliveHandler  extends ChannelInboundHandlerAdapter {

    private RpcCmd heartCmd;


    /**
     * 构造心跳消息
     */
    public KeepaliveHandler() {
        MessageDto messageDto = new MessageDto();
        messageDto.setCmd(MessageConstants.ACTION_HEART_CHECK);
        heartCmd = new RpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(MessageConstants.ACTION_HEART_CHECK + SnowflakeIdWorker.getInstance().nextId());
        heartCmd.setEvent(MessageConstants.ACTION_HEART_CHECK);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(heartCmd);
            }
        }
    }
}
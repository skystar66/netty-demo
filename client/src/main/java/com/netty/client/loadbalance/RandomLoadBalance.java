package com.netty.client.loadbalance;

import com.netty.client.client.channel.NettyChannelManager;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author xuliang
 */
@Component
public class RandomLoadBalance implements RpcLoadBalance {


    private Random random;

    public RandomLoadBalance() {
        random = new Random();
    }


    @Override
    public Channel getRemoteChannel() throws Exception {
        int size = NettyChannelManager.getInstance().currentSize();
        int randomIndex = random.nextInt(size);
        int index = 0;
        for (Channel channel : NettyChannelManager.getInstance().getChannelGroup()) {
            if (index == randomIndex) {
                return channel;
            }
            index++;
        }
        return null;
    }


}

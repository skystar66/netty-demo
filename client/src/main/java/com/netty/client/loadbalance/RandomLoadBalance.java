package com.netty.client.loadbalance;

import com.netty.client.pool.client.RpcClient;
import com.netty.client.pool.connect.ConnectionCache;
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
    public RpcClient getRpcClient() throws Exception {
        int size = ConnectionCache.rpcPoolSize();
        int randomIndex = random.nextInt(size);
        int index = 0;
        for (RpcClient client : ConnectionCache.clientMap.values()) {
            if (index == randomIndex) {
                return client;
            }
            index++;
        }
        return null;
    }


}

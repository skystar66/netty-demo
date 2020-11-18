package com.netty.client.pool;

import com.google.common.cache.LoadingCache;
import com.netty.client.config.TopicClientConfig;
import com.netty.core.client.init.RpcClientInitializer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ConnectionPoolFactory {


    @Autowired
    TopicClientConfig topicClientConfig;

    @Autowired
    RpcClientInitializer rpcClientInitializer;


    /**
     * 共享连接 nodeId ----> channels
     */
    public static final Map<Integer, Channel> sharedConnectPool =
            new ConcurrentHashMap<>();


    /**
     * 初始化连接
     */
    public void init() {
        for (int i = 0; i < topicClientConfig.getPoolSize(); i++) {
                //开始连接server端
                rpcClientInitializer.init(topicClientConfig.getHost(),
                        topicClientConfig.getPort(), true);
//                sharedConnectPool.put(i,channel);
        }
    }




    /**
     * 随机取
     */
    public Channel getChannelsRandom() {
//        int random = Randomut
        Random r = new Random(0);
//        return sharedConnectPool.get(nodeID);
        return sharedConnectPool.get(r.nextInt(topicClientConfig.getPoolSize()));
    }


//    /**
//     * 根据权重获取server 端服务
//     */
//
//    public ServerAdrWeight getServerRoute(int weight, LoadingCache<String, ServerAdrWeight> serverCacheMap) {
//        int random = RandomUtils.nextInt(0, weight);
//        int sum = 0;
//
//        for (Map.Entry<String, ServerAdrWeight> stringServerAdrWeightEntry : serverCacheMap.asMap().entrySet()) {
//            sum += stringServerAdrWeightEntry.getValue().getWeight();
//            if (sum > 0 && sum >= random) {
//                return stringServerAdrWeightEntry.getValue();
//            }
//        }
//        return null;
//    }

}

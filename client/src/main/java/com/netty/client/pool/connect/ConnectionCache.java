package com.netty.client.pool.connect;

import com.netty.client.pool.client.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ConnectionCache {

    public static ConcurrentMap<String, RpcClient> clientMap = new ConcurrentHashMap<String, RpcClient>();


    /**
     * 获取数量
     */
    public static int rpcPoolSize() {
        return clientMap.size();
    }

    public static RpcClient get(String key) {
        return clientMap.get(key);
    }

    public static void putIfAbsent(String key, RpcClient client) {

        clientMap.putIfAbsent(key, client);
        show();
    }

    public static void remove(String key) {
        clientMap.remove(key);
        show();
    }

    /**
     * 展示连接数量
     */
    public static void show() {
        log.info("####### 当前连接池数量: {}", clientMap.size());
    }
}

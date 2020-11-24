package com.netty.client.pool.connect;

import com.netty.client.pool.client.RpcClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionCache {

    public static ConcurrentMap<String, RpcClient> clientMap = new ConcurrentHashMap<String, RpcClient>();


    /**获取数量*/
    public static int rpcPoolSize(){
        return clientMap.size();
    }

    public static RpcClient get(String key) {
        return clientMap.get(key);
    }

    public static void putIfAbsent(String key, RpcClient client) {

        clientMap.putIfAbsent(key, client);
    }

    public static void remove(String key) {
        clientMap.remove(key);
    }
}

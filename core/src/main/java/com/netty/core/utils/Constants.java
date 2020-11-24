package com.netty.core.utils;

public class Constants {

    //通用config
    public static String CONFIG = "/config";

    public static String SERVER_CLUSTER = "/server/cluster";
    public static String SERVER_RPC_POOL_SIZE = "/server/rpcPoolSize";
    public static Integer SERVER_PORT = 9010;

    //缓存分隔符
    public static String SEQ = "__";

    //重试队列数量
    public static int retryQueueCount =1;

    //服务类型--环境变量
    public static String ENV_SERVER_TYPE="server.type";

}

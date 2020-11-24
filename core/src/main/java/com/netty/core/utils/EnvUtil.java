package com.netty.core.utils;

import com.netty.core.enums.EnvType;

public class EnvUtil {





    /**
     * 校验服务是否是客户端
     */
    public static boolean isClient() {
        return System.getProperty(Constants.ENV_SERVER_TYPE).equals(EnvType.CLIENT.name());
    }


    /**
     * 校验服务是否是服务端
     */
    public static boolean isServer() {
        return System.getProperty(Constants.ENV_SERVER_TYPE).equals(EnvType.SERVER.name());
    }


}

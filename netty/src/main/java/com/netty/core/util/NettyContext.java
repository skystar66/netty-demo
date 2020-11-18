package com.netty.core.util;


import com.netty.core.enums.NettyType;

/**
 * Description:
 * Company: CodingApi
 * Date: 2020/12/21
 *
 * @author xulia
 */
public class NettyContext {

    public static NettyType nettyType;


    public static NettyType currentType() {
        return nettyType;
    }

    public static Object params;

    public static <T> T currentParam(Class<T> tClass) {
        return (T) params;
    }


}

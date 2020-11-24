package com.netty.core.callback;

public interface CallBack {


    /**
     * 执行连接池重连逻辑
     */
    public void retryConnect(String rpcServer, Integer port, Integer index);

    /**
     * 删除连接池异常连接
     */

    public void removeConnect(String rpcPoolIndex);


}

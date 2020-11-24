package com.netty.client.client;


import com.netty.msg.dto.MessageDto;
import com.netty.msg.dto.RpcCmd;

/**
 * Description: 客户端api
 * Company: CodingApi
 * Date: 2020/2/10
 *
 * @author xuliang
 */
public abstract class ReqRpcClient {


    /**
     * 发送请求并获取响应
     *
     * @param messageDto 指令内容
     * @return 响应指令数据
     * @throws RpcException 远程调用请求异常
     */
    public abstract MessageDto request(RpcCmd rpcCmd) throws Exception;


    /**
     * 发送请求并获取响应
     *
     * @param remoteKey 远程标识关键字
     * @param msg       请求内容
     * @param timeout   超时时间
     * @return 响应消息
     * @throws RpcException 远程调用请求异常
     */
    public abstract MessageDto request(RpcCmd rpcCmd, long timeout) throws Exception;



    /**
     * 异步发送请求
     *
     * @param messageDto 指令内容
     * @return 响应指令数据
     * @throws RpcException 远程调用请求异常
     */
    public abstract void request2(RpcCmd rpcCmd) throws Exception;

}

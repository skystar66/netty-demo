package com.netty.msg.dto;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

//@Data
public class RpcCmd implements Serializable {

    /**
     * 请求唯一标识
     */

    private String key;

    /**
     * 远程标识关键字 netty 默认为 指定host
     * 默认分配 64457 个端口，形式为：ip:ranDomProt
     * todo remoteKey ??
     */
    private String remoteKey;


    /**
     * eventEnum
     */
    private String event;

    /**
     * 请求的消息内容体
     */
    private MessageDto msg;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRemoteKey() {
        return remoteKey;
    }

    public void setRemoteKey(String remoteKey) {
        this.remoteKey = remoteKey;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public MessageDto getMsg() {
        return msg;
    }

    public void setMsg(MessageDto msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

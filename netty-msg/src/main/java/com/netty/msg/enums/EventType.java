package com.netty.msg.enums;


import java.util.Arrays;

/**
 * 事件
 *
 * @author zhulei
 * @date 2017/9/19 17:34
 */

public enum EventType {


    /**
     * 心跳事件
     */
    heartCheck,

    // 请求事件
    /**
     * 订阅
     */
    SUB,
    /**
     * 取消订阅
     */
    CANCEL,
    /**
     * 登录
     */
    LOGIN,
    /**
     * 登出
     */
    LOGOUT,
    /**
     * 发送已读回执
     */
    ACK,
    /**
     * 删除消息
     */
    DELETE,

    // 业务响应事件
    /**
     * 正常响应
     */
    DATA,

    /**
     * 清空所有消息
     */
    CLEAR,
    /**
     * 推送广告列表
     */
    SUB_ADVERTISEMENT_LIST;

    public static EventType of(String type) {
        return Arrays.stream(EventType.values())
                .filter(eventType -> eventType.name().equalsIgnoreCase(type))
                .findFirst()
//            .orElseThrow(() ->new InvalidEventException("event "+ type +" not exist."));
                .orElse(null);
    }


}

package com.netty.msg;


/**
 * Description: cmd 指令
 * Date: 2018/12/6
 *
 * @author xuliang
 */

public class MessageConstants {

    /**
     * 心态检测
     */
    public static final String ACTION_HEART_CHECK = "heartCheck";

    /**
     * 发起请求状态
     */
    public static final int STATE_REQUEST = 100;

    /**
     * 响应成功状态
     */
    public static final int STATE_OK = 200;

    /**
     * 响应异常状态
     */
    public static final int STATE_EXCEPTION = 500;
}

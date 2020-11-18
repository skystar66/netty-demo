package com.netty.client.controller;


import com.alibaba.fastjson.JSONObject;
import com.netty.client.client.RpcClient;
import com.netty.core.util.SnowflakeIdWorker;
import com.netty.msg.MessageConstants;
import com.netty.msg.dto.MessageDto;
import com.netty.msg.dto.RpcCmd;
import com.netty.msg.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/client")
public class ClientController {


    @Autowired
    RpcClient rpcClient;


    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 发送
     */
    @RequestMapping("/send")
    public String send(@RequestParam("count") Integer count) throws Exception {
        for (int i=0;i<count;i++) {
            RpcCmd rpcCmd = new RpcCmd();
            rpcCmd.setKey(EventType.CANCEL.name() +
                    SnowflakeIdWorker.getInstance().nextId());
            MessageDto messageDto = new MessageDto();
            messageDto.setData("xuliang");
            messageDto.setState(MessageConstants.STATE_OK);
            rpcCmd.setMsg(messageDto);
            rpcCmd.setEvent(EventType.CANCEL.name());
             rpcClient.request2(rpcCmd);
        }
        return "success";
    }



}

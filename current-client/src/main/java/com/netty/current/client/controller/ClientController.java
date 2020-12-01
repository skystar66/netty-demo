package com.netty.current.client.controller;

import com.netty.current.client.config.TopicClientConfig;
import com.netty.current.client.current.CurrentClient;
import com.netty.core.client.init.RpcClientInitializer;
import com.netty.current.client.current.channel.NettyChannelManager;
import com.netty.current.client.mertic.MerticManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("client")
@Slf4j
public class ClientController {

    @Autowired
    RpcClientInitializer rpcClientInitializer;

    @Autowired
    TopicClientConfig topicClientConfig;



    @RequestMapping("test")
    public String testClient(@RequestParam("threadCnt") int threadCnt,
                             @RequestParam("connCnt") int connCnt) {
        CurrentClient client = new CurrentClient(rpcClientInitializer, topicClientConfig);

        try {
            client.start(connCnt);
        }catch (Exception ex) {

        }

        return "success";
    }



    @RequestMapping("getConnCount")
    public String getConnCount() {

        return String.valueOf(NettyChannelManager.getInstance().getConnCount());
    }

}

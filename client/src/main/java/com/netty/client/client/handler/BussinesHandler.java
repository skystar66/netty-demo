package com.netty.client.client.handler;

import com.netty.msg.RpcAnswer;
import com.netty.msg.dto.RpcCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class BussinesHandler implements RpcAnswer {


    @Override
    public void callback(RpcCmd rpcCmd) {
        log.info("recive msg : {}", rpcCmd);
    }
}

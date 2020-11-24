package com.netty.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfoVO {

    private String zkServerPath;
    private String rpcServer;
    private int rpcPort;
    private int rpcServerIndex;
    private int rpcPoolSize;

}

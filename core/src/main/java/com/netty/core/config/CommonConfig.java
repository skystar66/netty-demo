package com.netty.core.config;

import lombok.Data;

@Data
public class CommonConfig {



    private Integer rpcPoolSize;
    private Integer rpcRetryTimes;
    private Integer heartTime;


}

package com.netty.current.client.current;

import com.netty.core.server.handler.SocketManagerInitHandler;
import com.netty.current.client.config.TopicClientConfig;
import com.netty.core.client.init.RpcClientInitializer;
import com.netty.core.dto.ManagerProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CurrentClient {


    private ExecutorService executorService = Executors.newFixedThreadPool(50);

    RpcClientInitializer rpcClientInitializer;

    TopicClientConfig topicClientConfig;

    EventLoopGroup eventLoopGroup = null;
    Bootstrap bootstrap = null;

    public CurrentClient(RpcClientInitializer rpcClientInitializer, TopicClientConfig topicClientConfig) {
        this.rpcClientInitializer = rpcClientInitializer;
        this.topicClientConfig = topicClientConfig;
    }

    public void start(int count) throws Exception {

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
            }
        });

        for (int i = 0; i < count; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChannelFuture channelFuture = bootstrap.connect(topicClientConfig.getHost(),
                                topicClientConfig.getPort());
                        channelFuture.addListener((ChannelFutureListener) future -> {
                            if (!future.isSuccess()) {
                                System.out.println("connect failed, exit!");
//                                System.exit(0);
                            }
                        });
                        channelFuture.get();
                    } catch (Exception e) {
                    }
                }
            });
        }
    }


    public void start() throws Exception {
        // 2. 初始化RPC Server
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setCheckTime(20000);
        managerProperties.setRpcPort(topicClientConfig.getPort());
        managerProperties.setRpcHost(topicClientConfig.getHost());
        rpcClientInitializer.init(Arrays.asList(managerProperties),true);
    }

}

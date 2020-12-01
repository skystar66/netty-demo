package com.netty.core.server.init;

import com.netty.core.dto.ManagerProperties;
import com.netty.core.enums.NettyType;
import com.netty.core.server.handler.init.NettyRpcServerChannelInitializer;
import com.netty.core.util.NettyContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class RpcServerInitializer implements DisposableBean {


    @Autowired
    private NettyRpcServerChannelInitializer nettyRpcServerChannelInitializer;


    private EventLoopGroup workerGroup;
    private NioEventLoopGroup bossGroup;


    /**
     * support server init
     *
     * @param managerProperties 配置信息
     */
    public void init(ManagerProperties managerProperties) {
        //设置为服务端模型
        NettyContext.nettyType = NettyType.server;
        NettyContext.params = managerProperties;
        nettyRpcServerChannelInitializer.setManagerProperties(managerProperties);
        int port = managerProperties.getRpcPort();
        bossGroup = new NioEventLoopGroup(2);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyRpcServerChannelInitializer);

            // Start the server.
            if (StringUtils.hasText(managerProperties.getRpcHost())) {
                b.bind(managerProperties.getRpcHost(), managerProperties.getRpcPort());
            } else {
                b.bind(port);
            }
            log.info("Netty Socket Success started on {}:{} ",
                    StringUtils.hasText(managerProperties.getRpcHost()) ? managerProperties.getRpcHost() : "0.0.0.0", port);

        } catch (Exception e) {
            // Shut down all event loops to terminate all threads.
            e.printStackTrace();
        }
    }


    @Override
    public void destroy() throws Exception {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        log.info("server was down.");
    }


}

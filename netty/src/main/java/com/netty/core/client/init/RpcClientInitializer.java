package com.netty.core.client.init;

import com.netty.core.client.handler.init.NettyRpcClientChannelInitializer;
import com.netty.core.dto.ManagerProperties;
import com.netty.core.enums.NettyType;
import com.netty.core.util.NettyContext;
import com.netty.msg.manager.SocketChannelManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class RpcClientInitializer implements DisposableBean {


    @Autowired
    private NettyRpcClientChannelInitializer nettyRpcClientChannelInitializer;


    private EventLoopGroup workerGroup;


    public void init(List<ManagerProperties> hosts, boolean sync) throws Exception {
        NettyContext.nettyType = NettyType.client;
        NettyContext.params = hosts;
        workerGroup = new NioEventLoopGroup();
        for (ManagerProperties host : hosts) {
            Optional<Future> future = connect(new InetSocketAddress(host.getRpcHost(), host.getRpcPort()));
            log.info("Success Connect Topic Server Address : {}", host.getRpcHost() + ":" + host.getRpcPort());
            if (sync && future.isPresent()) {
                try {
                    future.get().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }


    public void init(String host, Integer port, boolean sync) throws Exception {
        NettyContext.nettyType = NettyType.client;
        NettyContext.params = host;
        workerGroup = new NioEventLoopGroup();
        Optional<Future> future = connect(new InetSocketAddress(host, port));
        log.info("Success Connect Idp Server Address : {}", host + ":" + port);
        if (sync && future.isPresent()) {
            try {
                future.get().get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public ChannelFuture initFuture(String host, Integer port, boolean sync){
        NettyContext.nettyType = NettyType.client;
        NettyContext.params = host;
        workerGroup = new NioEventLoopGroup();
        Optional<ChannelFuture> future = connect2(new InetSocketAddress(host, port));
        log.info("Success Connect Idp Server Address : {}", host + ":" + port);
        if (sync && future.isPresent()) {
            try {
                return future.get();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }


    public synchronized Optional<Future> connect(SocketAddress socketAddress) {

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        b.handler(nettyRpcClientChannelInitializer);
        return Optional.of(b.connect(socketAddress).syncUninterruptibly());

    }


    public synchronized Optional<ChannelFuture> connect2(SocketAddress socketAddress){
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        b.handler(nettyRpcClientChannelInitializer);
        return Optional.of(b.connect(socketAddress).syncUninterruptibly());
    }


    @Override
    public void destroy() throws Exception {
        workerGroup.shutdownGracefully();
        log.info("RPC client was down.");
    }


}

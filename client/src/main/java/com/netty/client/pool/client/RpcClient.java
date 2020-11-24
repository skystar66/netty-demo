package com.liveme.im.client.rpc;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liveme.common.utils.TimeFormat;
import com.liveme.im.core.codec.MessageDecoder;
import com.liveme.im.core.codec.MessageEncoder;
import com.liveme.im.core.constant.Constants;
import com.liveme.im.core.constant.ZkConfigHelper;
import com.liveme.im.core.protobuf.MessageBuf.SubTypeEnum;
import com.liveme.im.core.protobuf.MessageBuf.TypeEnum;
import com.liveme.im.core.protobuf.RpcBuf.Rpc;
import com.liveme.im.core.protocol.Packet;
import com.liveme.im.core.utils.AttributeKeys;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/** 
 * Rpc 连接登录
 * @author wgx
 * @version 2017年5月12日  
 */
public class RpcClient {
	
	private Logger log = LoggerFactory.getLogger(RpcClient.class);
	
	private String rpcServer; 
	private int rpcPort;
	private int index;
	private Bootstrap bootstrap;
	private EventLoopGroup group;
	private Channel channel;

	public RpcClient(String rpcServer, int rpcPort, int index){
		this.rpcServer = rpcServer;
		this.rpcPort = rpcPort;
		this.index = index;
		connection();
	}
	
	public void connection(){
		int rpcKeepaliveTime = ZkConfigHelper.getInstance().getImConfig().getRpcKeepaliveTime();
		try {
			group = new NioEventLoopGroup();
			bootstrap = new Bootstrap();
			bootstrap.group(group);
			
			bootstrap.channel(NioSocketChannel.class);
			
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			
			bootstrap.handler(new ChannelInitializer<SocketChannel>(){
				
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast("LiveMeDecoder", new MessageDecoder());
					p.addLast("LiveMeEncoder", MessageEncoder.INSTANCE);
					p.addLast("keepalive", new IdleStateHandler(0, rpcKeepaliveTime, 0));  
					p.addLast("handler", new RpcClientHandler());
				}
				
			});
			final ChannelFuture future = bootstrap.connect(rpcServer, rpcPort).sync();
			channel = future.channel();
			future.addListener(new ChannelFutureListener(){

				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					if(future.isSuccess()){
						loginRpcServer(channel.id().toString(), rpcServer, rpcPort, index);
					}
				}
				
			});

		} catch (InterruptedException e) {
			log.error("connection:{}",e);
		}
		
	}
	
	public static long getLongIp(String ip){
		long longIp = 0L;
		ip = ip.replace(".", "");
		longIp = Long.valueOf(ip);
		return longIp;
	}

	
	/**
	 * 登录Rpc服务
	 * @param channel
	 */
	public void loginRpcServer(String channelId, String rpcServer, int rpcPort,  int index){
		
		Rpc.Builder rpc = Rpc.newBuilder();
		rpc.setRpcServer(rpcServer);
		rpc.setRpcPort(rpcPort);
		rpc.setChannelId(channelId);
		rpc.setIndex(index);
		rpc.setTime(TimeFormat.getFormatDate(new Date(), TimeFormat.TIME_FORMAT_H));
		Rpc rpcBuf = rpc.build();
		
		Packet packet = new Packet(
				(byte)TypeEnum.LOGIN_VALUE, 
				(byte)SubTypeEnum.TEXT_VALUE, 
				0, 
				0, 
				rpcBuf.toByteArray() );
		
		this.sendMessage(packet);

		log.info("开始发送RPC客户端登录消息...");
		
		//channel上绑定rpc数据
		channel.attr(AttributeKeys.RPC_SERVER).set(rpcServer);
		channel.attr(AttributeKeys.RPC_PORT).set(rpcPort);
		channel.attr(AttributeKeys.RPC_INDEX).set(index);
		channel.attr(AttributeKeys.RPC_POOL_KEY).set(rpcServer + Constants.SEQ + rpcPort + Constants.SEQ + index);
		
		String key = rpcServer + Constants.SEQ + rpcPort + Constants.SEQ + index;

		
		log.info("loginRpcServer登录后的channel信息:rpcServer={}, , channel={}", channel.attr(AttributeKeys.RPC_SERVER).get(), channel);
	}
	
	
	/**
	 * 发送消息
	 * @param packet
	 */
	//TODO
//	public void sendMessage0(Packet packet){
//		final ChannelFuture future = channel.writeAndFlush(packet);
//		
//		//TODO log.info("send msg: packet={}, channel={}", packet, channel);
//		future.addListener(new ChannelFutureListener(){
//
//			@Override
//			public void operationComplete(ChannelFuture arg0) throws Exception {
//				if(future.isSuccess()){
//					//TODO log.info("消息发送成功！packet={}, channel={}", packet, channel);
//				}else {
//					channel.close();
//					//抛出异常以便于，重新发送
//					throw new Exception("发送结果失败");
//				}
//			}
//			
//		});
//	}
	
	/**
	 * 发送消息
	 * @param packet
	 */
	public void sendMessage(Packet packet){
		
		channel.writeAndFlush(packet);
		
		/*if(channel.isWritable()){
			channel.writeAndFlush(packet);
            //log.info("send packet success! packet={}", packet);
//			.addListener(future -> {
//                if (!future.isSuccess()) {
//                	log.warn("unexpected push. packet:{} fail:{}", packet, future.cause().getMessage());
//                }
//            });
        }else{
            try {
            	channel.writeAndFlush(packet).sync();
                log.info("sync send packet sended. remoteAddress:[{}]", channel.remoteAddress());
            } catch (InterruptedException e) {
            	log.info("write and flush msg exception. packet:[{}]",packet,e);
            }
        }*/
	}

	
}
//package com.jmeter.script.client;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;
//import redis.clients.jedis.Jedis;
//
//
//@Slf4j
//public class NettyClient extends AbstractJavaSamplerClient {
//
//    private SampleResult results;
//
//
//    private Jedis redis = null;
//
//    private static void start (String host,int port) throws InterruptedException {
//
//
////        String host = connectionInfoDomain.getHost();
////        int port = connectionInfoDomain.getPort();
//
//        log.info("准备建立连接: host - "+host+", post - "+port);
//
//
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true) // 不使用 Tcp 缓存
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline channelPipeline = ch.pipeline();
//                            // channelPipeline.addFirst(new StringDecoder(Charset.forName("UTF-8"))); // 添加字符串解码处理器
//                            // channelPipeline.addLast(new DeviceRegisterCommandHandler(deviceRegisterDomain));
//                            // channelPipeline.addLast(new AdCommandHandler());
//                            // channelPipeline.addLast(new AppUpdateHandler());
//                            // channelPipeline.addLast(new HeartBeatHandler());
//
//                            channelPipeline.addLast(new HeartBeatHandler( SSLUtils.getTerminalId(deviceRegisterDomain.getBusiness().getDevMac(), connectionInfoDomain) ));
//                        }
//                    });
//
//            ChannelFuture f = b.connect(host, port).sync();
//            f.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully();
//        }
//
//
//    }
//
//
//
//
//    @Override
//    public void setupTest(JavaSamplerContext context) {
//        results = new SampleResult();
//        results.setSamplerData(toString());
//        results.setDataType("text");
//        results.setContentType("text/plain");
//        results.setDataEncoding("UTF-8");
//
//        results.setSuccessful(true);
//        results.setResponseMessageOK();
//        results.setResponseCodeOK();
//
//    }
//
//    @Override
//    public SampleResult runTest(JavaSamplerContext arg0)  {
//        try {
//
//            results.sampleStart();
//
//
//            //RedisFactory.setJedis(connectionInfoDomain.getRedisHost(),connectionInfoDomain.getRedisPort());
//            Jedis redis = RedisFactory.getNewConnection(connectionInfoDomain.getRedisHost(),connectionInfoDomain.getRedisPort());
//            redis.select(1);
//            String threadNum = Thread.currentThread().getName().split("-")[1].trim();
//            int device_num = connectionInfoDomain.getBeginIndex() + Integer.parseInt(threadNum);
//            log.info("Current device num > "+device_num);
//
//            String mac = redis.hget("dev_mac", String.valueOf(device_num));
//            log.info("Current Mac > "+mac);
//            redis.close();
//
//            DeviceRegisterDomain deviceRegisterDomain = new DeviceRegisterDomain();
//            DeviceRegisterDomain.Business bussiness = new DeviceRegisterDomain.Business();
//            bussiness.setProductId("0208-4680-4892-1600");
//            bussiness.setAuthorizationCode("CB8B3AB7580C46528BA4E4AEF59DEB47");
//            bussiness.setDevMac(mac);
//            deviceRegisterDomain.setBusiness(bussiness);
//
//            NettyClient.start(connectionInfoDomain, deviceRegisterDomain);
//
//            results.sampleEnd();
//            return results;
//        } catch (Exception e) {
//            log.info("xxxxx"+e.getMessage());
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }
//}

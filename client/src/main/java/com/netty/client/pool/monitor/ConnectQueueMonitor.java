package com.netty.client.pool.monitor;

import com.netty.client.pool.manager.RpcClientManager;
import com.netty.core.mq.MQProvider;
import com.netty.core.mq.MessageQueue;
import com.netty.core.utils.Constants;
import com.netty.core.vo.ServerInfoVO;
import com.netty.msg.dto.RpcCmd;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * rpc 连接监控
 */
@Slf4j
public class ConnectQueueMonitor {


    private static class InstanceHolder {
        public static final ConnectQueueMonitor instance = new ConnectQueueMonitor();
    }

    public static ConnectQueueMonitor getInstance() {
        return ConnectQueueMonitor.InstanceHolder.instance;
    }


    private ExecutorService msgSenderExecutor;

    public ConnectQueueMonitor() {
    }

    public void start() {

        msgSenderExecutor = Executors.newFixedThreadPool(Constants.retryQueueCount);

        for (int i = 0; i < Constants.retryQueueCount; i++) {
            msgSenderExecutor.execute(new ConnectConsumerWorker(i));
        }
    }


    private class ConnectConsumerWorker implements Runnable {

        private final Duration timeout = Duration.ofMillis(100);
        private final MessageQueue<ServerInfoVO> retryConnectQueue;

        public ConnectConsumerWorker(int index) {
            this.retryConnectQueue = MQProvider.getRetryConnectQueueByIndex(index);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (null != retryConnectQueue) {
                        ServerInfoVO msg = retryConnectQueue.pop(timeout);
                        RpcClientManager.getInstance().connect(msg.getRpcServer(),
                                msg.getRpcPort(), msg.getRpcServerIndex());
                    }
                } catch (Exception ignore) {
                    log.warn("fromRPCMsgQueue.pop", ignore);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }


}

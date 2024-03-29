package com.netty.core.cluster;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkDataListener;
import com.netty.core.event.EventListener;
import com.netty.core.utils.Constants;
import com.netty.core.utils.SpringUtil;
import com.netty.core.vo.ServerInfoVO;
import com.netty.zookeeper.ZkHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Zk Cluster管理
 *
 * @author xl
 * @version 2020年11月20日
 */
public class ClusterCenter {

    private Logger log = LoggerFactory.getLogger(ClusterCenter.class);

    private static ZkHelp zkHelp = ZkHelp.getInstance();

    // 内部静态类方式
    private static class InstanceHolder {
        private static ClusterCenter instance = new ClusterCenter();
    }

    public static ClusterCenter getInstance() {
        return InstanceHolder.instance;
    }


    public ClusterCenter() {

        if (eventListener == null) {
            eventListener = SpringUtil.getBean(EventListener.class);
        }
    }

    public List<String> serverRpcList = null;
    public String rpcPoolSize = null;


    private EventListener eventListener;


    /**
     * Server RPC连接
     */
    public void listenerServerRpc() {
        serverRpcList = zkHelp.getChildren(Constants.SERVER_CLUSTER);
        log.info("serverRpcList:{}", serverRpcList);
        IZkChildListener listener = new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

                // 监听到子节点变化 更新cluster
                log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
                serverRpcList = currentChildren;
                ServerInfoVO serverInfoVO = ServerInfoVO.builder().zkServerPath(Constants.SERVER_CLUSTER)
                        .rpcPort(Constants.SERVER_PORT).build();
                eventListener.serverNodeChange(serverInfoVO);

            }
        };
        // 监控节点变更
        zkHelp.subscribeChildChanges(Constants.SERVER_CLUSTER, listener);
    }


    /**
     * Server RPC连接池监控
     */
    public void listenerServerRpcPoolSize() {
        rpcPoolSize = zkHelp.getValue(Constants.SERVER_RPC_POOL_SIZE);
        log.info("serverRpcPoolSize:{}", serverRpcList);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String parentPath, byte[] bytes) throws Exception {
                rpcPoolSize = new String(bytes);
                // 监听到子节点变化 更新cluster
                log.info("----->>>>> Starting rpcPoolSize data change " + parentPath + " rpcPoolSize=" + rpcPoolSize);
                eventListener.rpcPoolChange(Integer.parseInt(rpcPoolSize));
            }
            @Override
            public void handleDataDeleted(String s) throws Exception {
            }
        };
        // 监控节点变更
        zkHelp.subscribeDataChanges(Constants.SERVER_RPC_POOL_SIZE, listener);
    }

    /**
     * 获取Live服务IP
     * 根据hashCode取余
     *
     * @param flag
     * @return
     */
    public String getServerIp(long flag) {
        long num = Math.abs(flag) % serverRpcList.size();
        String ip = serverRpcList.get((int) num);
        return ip;
    }


}
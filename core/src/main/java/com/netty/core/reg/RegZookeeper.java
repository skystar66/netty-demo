package com.netty.core.reg;

import com.netty.core.utils.IpUtil;
import com.netty.zookeeper.ZkHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ZK注册IP
 * @author xl
 * @version 2020年11月20日
 */
public class RegZookeeper {
	
	static ZkHelp zk = ZkHelp.getInstance();
	
	private static Logger log = LoggerFactory.getLogger(RegZookeeper.class);
	
	/**
	 * 注册私网IP (为RPC提供调用服务)
	 */
	public void regIp(String clusterPath) {
		String zkPath = clusterPath;
		String privateIp = IpUtil.getLocalIP();
		log.info("本机IP地址 privateIp={}, awsLocalIp={}", privateIp);
		this.regIp(zkPath, privateIp);
	}
	
	
	public void regIp(final String zkPath, final String ip) {

		new Thread() {

			public void run() {
				boolean isFound = false;
				while (true) {
					isFound = false;
					List<String> servers = zk.getChildren(zkPath);
					for (String ser : servers) {
						if (ser.equals(ip)) {
							isFound = true;
							break;
						}
					}
					if (!isFound) {
						while (!zk.regInCluster(zkPath, ip)) {
							try {
								sleep(1000);
							} catch (InterruptedException e) {
							}
						}
					}
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}
	
}
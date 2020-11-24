package com.netty.zookeeper.reg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ZK注册IP
 * @author wgx
 * @version 2015年5月4日
 */
public class RegZookeeper {
	
	static ZkHelp zk = ZkHelp.getInstance();
	
	private static Logger log = LoggerFactory.getLogger(com.liveme.common.utils.RegZookeeper.class);
	
	/**
	 * 注册私网IP (为RPC提供调用服务)
	 */
	public void regIp(String clusterPath) {
		String zkPath = clusterPath;
		String privateIp = IpUtil.getLocalIP();

		String awsLocalIp = IpUtil.getAwsLocalIp();
		log.info("本机IP地址 privateIp={}, awsLocalIp={}", privateIp, awsLocalIp);
		if(null == awsLocalIp) {
			awsLocalIp = privateIp;
		}
		
		this.regIp(zkPath, awsLocalIp);
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
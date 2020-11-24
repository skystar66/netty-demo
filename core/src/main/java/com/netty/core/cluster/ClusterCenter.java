package com.liveme.im.client.cluster;

import java.util.List;

import org.liveme.zookeeper.ZkHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zkclient.IZkChildListener;
import com.liveme.im.client.rpc.RpcClientManager;
import com.liveme.im.core.constant.Constants;

/**
 * Zk Cluster管理
 * 
 * @author wgx
 * @version 2017年10月25日
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

	
	public List<String> liveRpcList = null;
	
	public List<String> livePublicClusterList = null;
	
	public List<String> webimRpcList = null;
	
	public List<String> webimPublicClusterList = null;
	
	public List<String> imRpcList = null;

	public List<String> chatRpcList = null;
	
	public List<String> groupChatRpcList = null;
	
	public List<String> roomRpcList = null;
	
	public List<String> apiRpcList = null;
	
	public List<String> pushRpcList = null;
	
	public List<String> taskRpcList = null;


	/**
	 * Live RPC连接
	 */
	public void initLiveRpc() {
		liveRpcList = zkHelp.getChildren(Constants.LIVE_CLUSTER);
		log.info("liveRpcList:{}", liveRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				liveRpcList = currentChildren;
				
				RpcClientManager.getInstance().zkSyncRpcServer(Constants.LIVE_CLUSTER, Constants.LIVE_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.LIVE_CLUSTER, listener);
	}

	/**
	 * 获取Live服务IP 
	 * 根据hashCode取余
	 * @param flag
	 * @return
	 */
	public String getLiveServerIp(long flag) {
		long num = Math.abs(flag) % liveRpcList.size();
		String ip = liveRpcList.get((int) num);
		return ip;
	}
	

	/**
	 * Live Public Clster连接
	 */
	public void initLivePublicClster() {
		livePublicClusterList = zkHelp.getChildren(Constants.LIVE_PUBLICCLUSTER);
		log.info("livePublicClusterList:{}", livePublicClusterList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				livePublicClusterList = currentChildren;
				
			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.LIVE_PUBLICCLUSTER, listener);
	}
	
	/**
	 * Live Public Clster连接
	 */
	public void initWebimPublicClster() {
		webimPublicClusterList = zkHelp.getChildren(Constants.WEBIM_PUBLICCLUSTER);
		log.info("webimPublicClusterList:{}", webimPublicClusterList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				webimPublicClusterList = currentChildren;
				
			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.WEBIM_PUBLICCLUSTER, listener);
	}

	/**
	 * 获取Live服务IP 
	 * 根据hashCode取余
	 * @param flag
	 * @return
	 */
	public String getLivePublicClster(long flag) {
		long num = Math.abs(flag) % livePublicClusterList.size();
		String ip = livePublicClusterList.get((int) num);
		return ip;
	}
	/**
	 * WEBIM RPC连接
	 */
	public void initWebIMRpc() {
		webimRpcList = zkHelp.getChildren(Constants.WEBIM_CLUSTER);
		log.info("webimRpcList:{}", webimRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				webimRpcList = currentChildren;
				
				RpcClientManager.getInstance().zkSyncRpcServer(Constants.WEBIM_CLUSTER, Constants.WEBIM_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.WEBIM_CLUSTER, listener);
	}

	/**
	 * 获取WebIM服务IP 
	 * 根据hashCode取余
	 * @param flag
	 * @return
	 */
	public String getWebIMServerIp(long flag) {
		long num = Math.abs(flag) % webimRpcList.size();
		String ip = webimRpcList.get((int) num);
		return ip;
	}
	
	
	
	public List<String> getWebimRpcList() {
		return webimRpcList;
	}
	
	
	public List<String> getWebimPublicClusterList() {
		return webimPublicClusterList;
	}
	
	
	/**
	 * Chat RPC连接
	 */
	public void initChatRpc() {
		chatRpcList = zkHelp.getChildren(Constants.CHAT_CLUSTER);
		log.info("chatRpcList:{}", chatRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				chatRpcList = currentChildren;

				RpcClientManager.getInstance().zkSyncRpcServer(Constants.CHAT_CLUSTER, Constants.CHAT_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.CHAT_CLUSTER, listener);
	}

	/**
	 * 获取CHAT服务IP
	 * 
	 * @param flag
	 * @return
	 */
	public String getChatServerIp(long flag) {
		long num = Math.abs(flag) % chatRpcList.size();
		String ip = chatRpcList.get((int) num);
		return ip;
	}
	
	/**
	 * GroupChat RPC连接
	 */
	public void initGroupChatRpc() {
		groupChatRpcList = zkHelp.getChildren(Constants.GROUPCHAT_CLUSTER);
		log.info("groupChatRpcList:{}", groupChatRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				groupChatRpcList = currentChildren;

				RpcClientManager.getInstance().zkSyncRpcServer(Constants.GROUPCHAT_CLUSTER, Constants.GROUPCHAT_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.GROUPCHAT_CLUSTER, listener);
	}

	/**
	 * 获取GroupChat服务IP
	 * 
	 * @param flag
	 * @return
	 */
	public String getGroupChatServerIp(long flag) {
		long num = Math.abs(flag) % groupChatRpcList.size();
		String ip = groupChatRpcList.get((int) num);
		return ip;
	}
	
	/**
	 * Room RPC连接
	 */
	public void initRoomRpc() {
		roomRpcList = zkHelp.getChildren(Constants.ROOM_CLUSTER);
		log.info("roomRpcList:{}", roomRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				roomRpcList = currentChildren;

				RpcClientManager.getInstance().zkSyncRpcServer(Constants.ROOM_CLUSTER, Constants.ROOM_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.ROOM_CLUSTER, listener);
	}

	/**
	 * 获取ROOM服务IP
	 * 
	 * @param flag
	 * @return
	 */
	public String getRoomServerIp(long flag) {
		long num = Math.abs(flag)% roomRpcList.size();
		String ip = roomRpcList.get((int) num);
		return ip;
	}

	
	/**
	 * Api RPC连接
	 */
	public void initApiRpc() {
		apiRpcList = zkHelp.getChildren(Constants.API_CLUSTER);
		log.info("apiRpcList:{}", apiRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				apiRpcList = currentChildren;

				RpcClientManager.getInstance().zkSyncRpcServer(Constants.API_CLUSTER, Constants.API_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.API_CLUSTER, listener);
	}

	/**
	 * 获取Api服务IP
	 * 
	 * @param flag
	 * @return
	 */
	public String getApiServerIp(long flag) {
		long num = Math.abs(flag) % apiRpcList.size();
		String ip = apiRpcList.get((int) num);
		return ip;
	}

	/**
	 * Push RPC连接
	 */
	public void initPushRpc() {
		pushRpcList = zkHelp.getChildren(Constants.PUSH_CLUSTER);
		log.info("pushRpcList:{}", pushRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				pushRpcList = currentChildren;

				RpcClientManager.getInstance().zkSyncRpcServer(Constants.PUSH_CLUSTER, Constants.PUSH_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.PUSH_CLUSTER, listener);
	}

	/**
	 * 获取Push服务IP
	 * 
	 * @param flag
	 * @return
	 */
	public String getPushServerIp(long flag) {
		long num = Math.abs(flag) % pushRpcList.size();
		String ip = pushRpcList.get((int) num);
		return ip;
	}
	
	/**
	 * Task RPC连接
	 */
	public void initTaskRpc() {
		taskRpcList = zkHelp.getChildren(Constants.TASK_CLUSTER);
		log.info("taskRpcList:{}", taskRpcList);
		IZkChildListener listener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {

				// 监听到子节点变化 更新cluster
				log.info("----->>>>> Starting handle children change " + parentPath + "/" + currentChildren + " size=" + currentChildren.size());
				taskRpcList = currentChildren;

				RpcClientManager.getInstance().zkSyncRpcServer(Constants.TASK_CLUSTER, Constants.TASK_RPC_PORT);

			}
		};
		// 监控节点变更
		zkHelp.subscribeChildChanges(Constants.TASK_CLUSTER, listener);
	}

	/**
	 * 获取Task服务IP
	 * 
	 * @param flag
	 * @return
	 */
	public String getTaskServerIp(long flag) {
		long num = Math.abs(flag) % taskRpcList.size();
		String ip = taskRpcList.get((int) num);
		return ip;
	}

}
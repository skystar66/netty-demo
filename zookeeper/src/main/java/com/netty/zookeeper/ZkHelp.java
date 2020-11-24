package org.liveme.zookeeper;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.liveme.zookeeper.bean.Env;
import org.liveme.zookeeper.digest.RSACrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkInterruptedException;

/**
 * Zookeeper API
 * 
 * @author wgx
 * @version 2017年10月18日
 */
public class ZkHelp {
	private final static Logger logger = LoggerFactory.getLogger(ZkHelp.class);

	private static class InstanceHolder {
		private static final ZkHelp instance = new ZkHelp();
	}

	private ZkHelp() {
		if (Env.isUnit()) {// 单元测试不依赖zk
			return;
		}
		init();
	}

	public static ZkHelp getInstance() {
		return InstanceHolder.instance;
	}

	// 环境标志
	private String environmentFlag = "";
	private String scheme = "digest";
	private boolean isOp = false;
	private String privateKeyFile = "/root/.ssh/cjet_pri.pkcs8";
	private String publicKeyFile = "";

	public ZkClient client = null;
	private final static String envRegex = "(/\\w+){2}";
	// zookeeper集群地址 开发环境
	public String zooKeeperCluster = "";

	public int sessionTimeout = 60000;
	public int connectionTimeout = 60000;

	/**
	 * 初始化
	 */
	private void init() {
		try {
			// 测试环境
			zooKeeperCluster = getZkCluster();

			client = new ZkClient(zooKeeperCluster, sessionTimeout, connectionTimeout);

			environmentFlag = Env.getEnvironmentFlag();
			logger.info("environmentFlag={}", environmentFlag);
			if (environmentFlag == null || "".equals(environmentFlag)) {
				throw new RuntimeException("environmentFlag should not be empty");
			}
			if (!environmentFlag.matches(envRegex)) {
				throw new RuntimeException(environmentFlag + " is not a right environmentFlag :" + envRegex);
			}

			ZooKeeper zooKeeper = client.getZooKeeper();
			// 授权
			String publicKeyFilePath = getPublicKeyFilePath();
			if (!"".equals(publicKeyFilePath)) {
				if (!RSACrypto.verify(publicKeyFilePath)) {
					throw new RuntimeException("invalid publicKeyFile:" + publicKeyFilePath);
				}
				isOp = true;
				this.publicKeyFile = publicKeyFilePath;
			} else {
				isOp = false;
			}
			// 设置当前session的权限
			String authInfo = "chanjetzk:" + environmentFlag.split("/")[1];
			if (isOp) {
				authInfo = "chanjetop:chanjetop";
			}
			zooKeeper.addAuthInfo(scheme, authInfo.getBytes("UTF-8"));

			String rootPath = environmentFlag;
			this.addNode(rootPath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设置路径访问权限
	 *
	 * @param isEncrypted 如果是加密节点，则应用只有读的权限
	 * @throws Exception
	 */
	private void setAuth(ZooKeeper zooKeeper, String path, boolean isEncrypted) throws Exception {
		List<ACL> aclList = new ArrayList<>();
		Id id = new Id("world", "anyone");
		ACL acl = new ACL(ZooDefs.Perms.ALL, id);
		aclList.add(acl);
		if (isEncrypted) {
			// 运维有所有节点的读写权限
			String authInfo = "chanjetzk:" + Env.getEnvironmentFlag().split("/")[1];
			id = new Id(scheme, DigestAuthenticationProvider.generateDigest(authInfo));
			acl = new ACL(ZooDefs.Perms.READ, id);
			aclList.add(acl);
		}
		zooKeeper.setACL(path, aclList, -1);
		// 暂时取消节点加权限（需要时放开这段代码即可）
		// List<ACL> aclList = new ArrayList<>();
		// String authInfo = "chanjetzk:" + Env.getEnvironmentFlag().split("/")[1];
		// Id id = new Id(scheme,
		// DigestAuthenticationProvider.generateDigest(authInfo));
		//
		// ACL acl = new ACL(ZooDefs.Perms.ALL, id);;
		// if (!isEncrypted) {
		// acl = new ACL(ZooDefs.Perms.ALL, id);
		// } else {
		// acl = new ACL(ZooDefs.Perms.READ, id);
		// }
		// aclList.add(acl);
		// // 运维有所有节点的读写权限
		// authInfo = "chanjetop:chanjetop";
		// id = new Id(scheme,
		// DigestAuthenticationProvider.generateDigest(authInfo));
		// acl = new ACL(ZooDefs.Perms.ALL, id);
		// aclList.add(acl);
		// zooKeeper.setACL(path, aclList, -1);
		return;
	}

	/**
	 * 重建实例
	 */
	public void reInit() {
		closeZk();
		init();
	}

	/**
	 * 私钥路径,默认当前用户.ssh目录下的id_rsa 允许某些没有加密节点的应用存在，则这些应用不必有私钥，所以不放在init里初始化
	 * 
	 */

	private String getPrivateKeyFilePath() {
		String pkFile = System.getProperty("config.privateKeyFile");
		pkFile = (pkFile == null || "".equals(pkFile)) ? privateKeyFile : pkFile;
		File file = new File(pkFile);
		if (!file.exists()) {
			throw new RuntimeException("privateKeyFile not exist:" + privateKeyFile);
		}
		return pkFile;
	}

	/**
	 * 公钥路径,默认当前用户.ssh目录下的id_rsa
	 *
	 */

	private String getPublicKeyFilePath() {
		if (publicKeyFile != null && !"".equals(publicKeyFile)) {
			return publicKeyFile;
		}
		String pkFile = System.getProperty("config.publicKeyFile");
		pkFile = (pkFile == null || "".equals(pkFile)) ? "" : pkFile;
		File file = new File(pkFile);
		if (!file.exists()) {
			return "";
		}
		// TODO check publicKey
		return pkFile;
	}

	/**
	 * 检查是否需要增加环境标示
	 *
	 * @param path 环境标示
	 * @return boolean
	 */
	private boolean checkEnv(String path) {
		return path.startsWith(environmentFlag);
	}

	// WangJianMark zkConfigCenter inte
	// 172.18.19.52:2181,172.18.19.53:2181,172.18.19.54:2181
	private String getZkCluster() {
		String zooKeeperCluster = System.getProperty("config.zkCluster");
		if (zooKeeperCluster == null || "".equals(zooKeeperCluster)) {
			// 测试环境
			if (Env.isInte()) {
				// zooKeeperCluster = "127.0.0.1:2181";
				zooKeeperCluster = "10.61.153.47:2181,10.61.153.47:2181";
				// zooKeeperCluster =
				// "172.16.200.12:2181,172.16.200.18:2181,172.16.200.19:2181";
			} else if (Env.isMoni()) {// 模拟环境
				zooKeeperCluster = "10.66.100.243:2181,10.66.100.243:2182,10.66.100.243:2183";
//            zooKeeperCluster = "34.192.46.114:2181,34.192.46.114:2182,34.192.46.114:2183";
			} else if (Env.isPre()) {// 预发布环境
				zooKeeperCluster = "10.66.100.243:2181,10.66.100.243:2182,10.66.100.243:2183";
//            zooKeeperCluster = "34.192.46.114:2181,34.192.46.114:2182,34.192.46.114:2183";
			} else if (Env.isProd()) {// 生产环境
				zooKeeperCluster = "10.66.100.107:2181,10.66.100.177:2181,10.66.100.102:2181,10.66.102.84:2181,10.66.102.132:2181";
//          zooKeeperCluster = "35.169.152.251:2181,52.21.69.102:2181,52.7.219.37:2181,34.239.212.153:2181,35.169.193.206:2181";
				// zooKeeperCluster =
				// "172.16.20.5:2181,172.16.20.6:2181,172.16.20.7:2181,172.16.41.57:2181,172.16.41.58:2181";
			} else if (Env.isCmShow()) {// 豹来电cmshow生产环境
				zooKeeperCluster = "10.46.122.54:2181,10.46.122.134:2181,10.46.122.136:2181";
//        zooKeeperCluster = "35.169.152.251:2181,52.21.69.102:2181,52.7.219.37:2181,34.239.212.153:2181,35.169.193.206:2181";
				// zooKeeperCluster =
				// "172.16.20.5:2181,172.16.20.6:2181,172.16.20.7:2181,172.16.41.57:2181,172.16.41.58:2181";
			} else if (Env.isMeast()) {// 中东数据中心测试生产环境
				zooKeeperCluster = "52.58.139.118:2181";
				//zooKeeperCluster = "52.58.139.118:2181";
			} else {
				zooKeeperCluster = "10.60.82.178:2181,10.60.82.179:2181,10.60.82.180:2181";
				// zooKeeperCluster =
				// "172.16.200.12:2182,172.16.200.18:2182,172.16.200.19:2182";
			}
		}
		logger.info("zooKeeperCluster={}", zooKeeperCluster);
		return zooKeeperCluster;
	}

	/**
	 * 关闭zk实例
	 */
	private void closeZk() {
		try {
			client.close();
		} catch (ZkInterruptedException e) {
			logger.error("zookeeper client  close error!", e);
		}
	}

	/**
	 * 删除节点
	 */
	public void delete(String path) {
		try {
			if (!checkEnv(path))
				path = environmentFlag + path;

			if (isEncrpted(path) && !isOp) {
				throw new RuntimeException("NoAuth to delete path:" + path);
			}
			if (client.exists(path) && !client.delete(path)) {
				throw new RuntimeException("zk delete node failed:" + path);
			}

		} catch (Exception e) {
			logger.error("zookeeper client  delete error!", e);
			throw new RuntimeException("delete exception:" + e.getMessage());
		}
	}

	/**
	 * 添加zk节点,默认是不加密节点
	 */
	public void addNode(String path) {
		this.addNode(path, false);
	}

	/**
	 * 添加zk节点
	 */
	public void addNode(String path, boolean isEncrypted) {
		try {
			if (!checkEnv(path))
				path = environmentFlag + path;
			ZooKeeper zooKeeper = client.getZooKeeper();
			if (client.exists(path)) {
				setAuth(zooKeeper, path, isEncrypted);
				return;
			}
			// 初始化权限信息

			String tmpPath = "";
			String array[] = path.split("/");
			int length = array.length;
			for (String anArray : array) {
				if (anArray.equals("")) {
					continue;
				}
				tmpPath = tmpPath + "/" + anArray;
				// 节点不存在先创建
				if (!client.exists(tmpPath)) {
					client.createPersistent(tmpPath, null);
				}
			}
			setAuth(zooKeeper, path, isEncrypted);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * 节点数据变化监听
	 */
	public void subscribeDataChanges(String path, IZkDataListener listener) {
		if (Env.isUnit()) {// 单元测试不依赖zk
			return;
		}
		if (!checkEnv(path))
			path = environmentFlag + path;
		client.subscribeDataChanges(path, listener);
	}

	/**
	 * 节点子节点变化监听
	 */
	public void subscribeChildChanges(String path, IZkChildListener listener) {
		if (Env.isUnit()) {// 单元测试不依赖zk
			return;
		}
		if (!checkEnv(path))
			path = environmentFlag + path;
		client.subscribeChildChanges(path, listener);
	}

	/**
	 * 获取path节点下的儿子节点列表
	 */
	public List<String> getChildren(String path) {
		if (!checkEnv(path))
			path = environmentFlag + path;
		if (client.exists(path)) {
			return client.getChildren(path);
		}
		logger.info("zookeeper NoNode exists for " + path);
		return new ArrayList<>();
	}

	/**
	 * 判断节点是否加密
	 *
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public boolean isEncrpted(String path) throws KeeperException, InterruptedException {
		List<ACL> list = client.getZooKeeper().getACL(path, new Stat());
		if (list != null && list.size() > 0) {
			for (ACL acl : list) {
				Id id = acl.getId();
				if (scheme.equals(id.getScheme()) && "chanjetzk".equals(id.getId().split(":")[0])
						&& Perms.READ == acl.getPerms()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取节点数据
	 */
	public String getValue(String path) {
		if (Env.isUnit()) {// 单元测试不依赖zk
			return "";
		}
		if (!checkEnv(path))
			path = environmentFlag + path;
		try {
			if (client.exists(path)) {
				byte data[] = client.readData(path);
				if (data == null) {
					return null;
				}
				if (isEncrpted(path)) {
					return decrypt(data);
				} else {
					return new String(data, "UTF-8");
				}

			}
		} catch (Exception e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}

		logger.info("zookeeper NoNode exists for " + path);
		return null;
	}

	/**
	 * thrift server 启动服务时候调增加临时节点
	 */
	public boolean regInCluster(String path, String serverName) {
		String newPath = path + "/" + serverName;
		logger.info(">>>>> start register " + newPath + " in cluster <<<<<");
		setPathData(path, null);
		boolean b = createEphemeral(newPath, serverName);
		if (b) {
			logger.info("Servers:" + getChildren(path));
			logger.info(">>>>> register server " + serverName + " ok <<<<<");
		}
		return b;
	}

	/**
	 * 创建临时节点
	 */
	public boolean createEphemeral(String path, String value) {
		if (!checkEnv(path))
			path = environmentFlag + path;
		boolean b = false;
		try {
			client.createEphemeral(path, value.getBytes("UTF-8"));
			b = true;
		} catch (Exception e) {
			logger.error("!!!! register " + path + " in cluster error !!!", e);
		}
		return b;
	}

	/**
	 * 节点设置值
	 *
	 * @param path  路径
	 * @param value 值
	 */
	public void setPathData(String path, String value) {
		if (!checkEnv(path))
			path = environmentFlag + path;
		this.addNode(path);
		client.writeData(path, value == null ? null : value.getBytes(Charset.forName("UTF-8")));
	}

	/**
	 * 节点设置值
	 *
	 * @param path        路径
	 * @param value       值
	 * @param isEncrypted 是否加密，只有运维有公钥可以执行加密操作
	 */
	public void setPathData(String path, String value, boolean isEncrypted) {
		if (!checkEnv(path))
			path = environmentFlag + path;
		this.addNode(path, isEncrypted);
		value = isEncrypted ? encrypt(value) : value;
		client.writeData(path, value == null ? null : value.getBytes(Charset.forName("UTF-8")));
	}

	public String decrypt(byte b[]) {
		if (b == null) {
			return null;
		}
		try {
			return RSACrypto.decryptByPrivateKey(new String(b, "UTF-8"), getPrivateKeyFilePath());
		} catch (Exception e) {
			logger.error("", e);
			throw new RuntimeException("decrypt exception:" + e.getMessage());
		}
	}

	private String encrypt(String value) {
		if ("".equals(this.publicKeyFile)) {
			throw new RuntimeException("no publicKeyFile found ");
		}
		try {
			return RSACrypto.encryptByPublicKey(value, publicKeyFile);
		} catch (Exception e) {
			logger.error("", e);
			throw new RuntimeException("decrypt exceptin:", e);
		}
	}

	/**
	 * 判断节点是否存在
	 *
	 */
	public boolean exists(String path) {
		if (!checkEnv(path))
			path = environmentFlag + path;
		return client.exists(path);
	}

	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	public void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

	public String getPublicKeyFile() {
		return publicKeyFile;
	}

	public void setPublicKeyFile(String publicKeyFile) {
		this.publicKeyFile = publicKeyFile;
	}

}

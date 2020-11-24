package com.liveme.im.core.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.liveme.zookeeper.ZkHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.zkclient.IZkDataListener;
import com.liveme.common.utils.BeanJsonUtil;
import com.liveme.common.utils.StringUtil;

/** 
 * zk 动态监听配置
 * @author wgx
 * @version 2017年11月30日 
 */
public class ZkConfigHelper {

	private final static Logger log = LoggerFactory.getLogger(ZkConfigHelper.class);

	private final static String configZkPath = Constants.CONFIG;

	private KafkaConfig kafkaConfig = null;
	private IMConfig imConfig = null;
	private TokenServiceConfig tokenServiceConfig = null;
	private ChatMillionConfig chatMillionConfig = null;
	private GroupChatPushConfig groupChatPushConfig = null;
	private ScreenOutMessage screenOutMessage  = new ScreenOutMessage();;
	
//	private Con
	

	private ZkHelp zkHelp = ZkHelp.getInstance();
	
	private static IZkDataListener listenerGlobal = null;

	private static class InstanceHolder {
		private static final ZkConfigHelper instance = new ZkConfigHelper();;
	}

	public static ZkConfigHelper getInstance() {
		return InstanceHolder.instance;
	}

	private ZkConfigHelper() {
		listenerGlobal = new IZkDataListener() {
			@Override
			public void handleDataChange(String dataPath, byte[] data) throws Exception {
				log.info("!!! configZkPath node data has been changed !!!" + dataPath);
				String rtdata = null;
				if (data != null && data.length > 0) {
					rtdata = new String(data, "UTF-8");
					JSONObject json = JSONObject.parseObject(rtdata);

					// read kafkaconfig
					String kafkaNode = json.getString("kafkaconfig");
					kafkaConfig = BeanJsonUtil.toBean(kafkaNode, KafkaConfig.class);
					
					// read imconfig
					String configNode = json.getString("imconfig");
					imConfig = BeanJsonUtil.toBean(configNode, IMConfig.class);
					
					String tokenService = json.getString("tokenServiceconfig");
					if(StringUtil.isNotEmpty(tokenService)){
						tokenServiceConfig =  BeanJsonUtil.toBean(tokenService, TokenServiceConfig.class);
					}
					
					String groupChatPushConfigj = json.getString("groupChatPushConfig");
					if(StringUtil.isNotEmpty(groupChatPushConfigj)){
						groupChatPushConfig =  BeanJsonUtil.toBean(groupChatPushConfigj, GroupChatPushConfig.class);
					}
					
					if(json.containsKey("screenOutAppids")) {
						List<String> screenOutAppids = json.getObject("screenOutAppids",new TypeReference<List<String>>(){});
						screenOutMessage.setScreenOutAppids(screenOutAppids);
						if(null != screenOutAppids && screenOutAppids.size() > 0) {
							// read screentOutMessageType  查询屏蔽 某一个 app下  屏蔽的消息类型
							if(json.containsKey("screenOutMessageTypeOfAppids")) {
								Map<String,List<Integer>> screenOutMessageType=json.getObject("screenOutMessageTypeOfAppids", new TypeReference<Map<String, List<Integer>>>(){});
								screenOutMessage.setScreenOutMessageTypeOfAppids(screenOutMessageType);
							}
						}
						
					}
					//添加按appid类型的消息统计,和日志消息输出
					if(json.containsKey("StatisticMessageConfig")) {
						JSONObject statisticMessageConfigJsonObject=json.getJSONObject("StatisticMessageConfig");
						if(statisticMessageConfigJsonObject.containsKey("isOpenAppMessageLogWirte")) {
							StatisticMessageConfig.setIsOpenAppMessageLogWirte(statisticMessageConfigJsonObject.getBoolean("isOpenAppMessageLogWirte"));
						}
						if(statisticMessageConfigJsonObject.containsKey("isOpenAppMessageNumStatistic")) {
							StatisticMessageConfig.setIsOpenAppMessageNumStatistic(statisticMessageConfigJsonObject.getBoolean("isOpenAppMessageNumStatistic"));
							if(statisticMessageConfigJsonObject.containsKey("AppMessageNumStatisticSleepSecond")) {
								StatisticMessageConfig.setAppMessageNumStatisticSleepSecond(statisticMessageConfigJsonObject.getIntValue("AppMessageNumStatisticSleepSecond"));
							}
						}
						
					}
					
				}
				log.info("!!! configZkPath node data has been changed ok !!!" + rtdata + ", kafkaConfig=[" + kafkaConfig + "]");
			}

			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				log.info("!!! configZkPath node dataPath has been delete !!!" + dataPath + ", kafkaConfig=[" + kafkaConfig + "]");
			}
		};
		// 添加节点监控
		zkHelp.subscribeDataChanges(configZkPath, listenerGlobal);
		try {
			
			String rtdata = new String(zkHelp.getValue(configZkPath));
			JSONObject json = JSONObject.parseObject(rtdata);

			// read kafkaconfig
			String kafkaNode = json.getString("kafkaconfig");
			kafkaConfig = BeanJsonUtil.toBean(kafkaNode, KafkaConfig.class);
			
			// read imconfig
			String configNode = json.getString("imconfig");
			imConfig = BeanJsonUtil.toBean(configNode, IMConfig.class);
			
			// read tokenServiceconfig
			String tokenService = json.getString("tokenServiceconfig");
			if(StringUtil.isNotEmpty(tokenService)){
				tokenServiceConfig =  BeanJsonUtil.toBean(tokenService, TokenServiceConfig.class);
			}
			
			String groupChatPushConfigj = json.getString("groupChatPushConfig");
			if(StringUtil.isNotEmpty(groupChatPushConfigj)){
				groupChatPushConfig =  BeanJsonUtil.toBean(groupChatPushConfigj, GroupChatPushConfig.class);
			}
			
			// read  appid  读取屏蔽消息的 app 类型，
			if(json.containsKey("screenOutAppids")) {
				screenOutMessage =new ScreenOutMessage();
				List<String> screenOutAppids = json.getObject("screenOutAppids",new TypeReference<List<String>>(){});
				screenOutMessage.setScreenOutAppids(screenOutAppids);
				if(null != screenOutAppids && screenOutAppids.size() > 0) {
					// read screentOutMessageType  查询屏蔽 某一个 app下  屏蔽的消息类型
					if(json.containsKey("screenOutMessageTypeOfAppids")) {
						Map<String,List<Integer>> screenOutMessageType=json.getObject("screenOutMessageTypeOfAppids", new TypeReference<Map<String, List<Integer>>>(){});
						screenOutMessage.setScreenOutMessageTypeOfAppids(screenOutMessageType);
					}
				}
				
			}
			
			//添加按appid类型的消息统计,和日志消息输出
			if(json.containsKey("StatisticMessageConfig")) {
				JSONObject statisticMessageConfigJsonObject=json.getJSONObject("StatisticMessageConfig");
				
				//是否开启消息日志输出
				if(statisticMessageConfigJsonObject.containsKey("isOpenAppMessageLogWirte")) {
					StatisticMessageConfig.setIsOpenAppMessageLogWirte(statisticMessageConfigJsonObject.getBoolean("isOpenAppMessageLogWirte"));
				}
				
				//是否开启消息统计日志输出
//				if(statisticMessageConfigJsonObject.containsKey("isOpenAppMessageNumStatistic")) {
//					StatisticMessageConfig.setIsOpenAppMessageNumStatistic(statisticMessageConfigJsonObject.getBoolean("isOpenAppMessageNumStatistic"));
//					if(statisticMessageConfigJsonObject.containsKey("AppMessageNumStatisticSleepSecond")) {
//						StatisticMessageConfig.setAppMessageNumStatisticSleepSecond(statisticMessageConfigJsonObject.getIntValue("AppMessageNumStatisticSleepSecond"));
//						AppMessageNumStatistic.startMessageCountLogWrite();
//					}
//				}
				
			}
			
			
		} catch (Exception e) {
			log.error("", e);
		}
		log.info("===================init ZkConfigHelper ok================");
	}
	
	public ChatMillionConfig getChatMillionConfig() {
		return chatMillionConfig;
	}

	public void setChatMillionConfig(ChatMillionConfig chatMillionConfig) {
		this.chatMillionConfig = chatMillionConfig;
	}

	public KafkaConfig getKafkaConfig() {
		return kafkaConfig;
	}

	public void setKafkaConfig(KafkaConfig kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}
	
	public IMConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(IMConfig imConfig) {
		this.imConfig = imConfig;
	}

	public ZkHelp getZkHelp() {
		return zkHelp;
	}

	public void setZkHelp(ZkHelp zkHelp) {
		this.zkHelp = zkHelp;
	}

	public TokenServiceConfig getTokenServiceConfig() {
		return tokenServiceConfig;
	}

	public void setTokenServiceConfig(TokenServiceConfig tokenServiceConfig) {
		this.tokenServiceConfig = tokenServiceConfig;
	}

	public GroupChatPushConfig getGroupChatPushConfig() {
		return groupChatPushConfig;
	}

	public void setGroupChatPushConfig(GroupChatPushConfig groupChatPushConfig) {
		this.groupChatPushConfig = groupChatPushConfig;
	}

	public ScreenOutMessage getScreenOutMessage() {
		return screenOutMessage;
	}

	public void setScreenOutMessage(ScreenOutMessage screenOutMessage) {
		this.screenOutMessage = screenOutMessage;
	}

//	public RouterConfig getRouterconfig() {
//		return routerConfig;
//	}
//
//	public void setRouterconfig(RouterConfig routerconfig) {
//		this.routerConfig = routerconfig;
//	}

	
	public static void main(String[] args) {
		List<String> data=new ArrayList<String>();
		data.add("liveme");
		data.add("liveme2");
		
		System.out.println(JSONObject.toJSONString(data));
		
		List<Integer> screenOutMessageType=new ArrayList<Integer>();
		screenOutMessageType.add(1);
		screenOutMessageType.add(3);
		screenOutMessageType.add(4);
		
		
		Map<String,Object> screenOutMessageOfMap=new HashMap<String,Object>();
		
		screenOutMessageOfMap.put("liveme", screenOutMessageType);
		screenOutMessageOfMap.put("liveme2", screenOutMessageType);
		
		System.out.println(JSONObject.toJSONString(screenOutMessageOfMap));
		
	}
}
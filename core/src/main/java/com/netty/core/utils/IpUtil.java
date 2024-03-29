package com.netty.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpUtil {
	private static final Logger log = LoggerFactory.getLogger(IpUtil.class);

	/***
	 * 获取客户端IP地址;这里通过了Nginx获取;X-Real-IP,
	 * 
	 * @param request
	 * @return
	 */
	public static String getClientIP(HttpServletRequest request) {
		String fromSource = "X-Forwarded-For";
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
			fromSource = "X-Real-IP";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			fromSource = "Proxy-Client-IP";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			fromSource = "WL-Proxy-Client-IP";
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			fromSource = "request.getRemoteAddr";
		}
		
		if(StringUtils.isNotBlank(ip) && ip.contains(",")) {
			ip = ip.split(",")[0];
		}
		//log.info("App Client IP: " + ip + ", fromSource: " + fromSource);
		return ip;
	}
	/**
	 * 获取本机IP地址，并自动区分Windows还是Linux操作系统
	 *
	 * @return String
	 */
	public static String getLocalIP() {
		String sIP = "";
		InetAddress ip = null;
		List<InetAddress> innerIpList = new ArrayList<InetAddress>();
		try {
			// 如果是Windows操作系统
			if (false) {// isWindowsOS()) {
				ip = InetAddress.getLocalHost();
				innerIpList.add(ip);
			}
			// 如果是Linux操作系统
			else {
				Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
						.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
					// ----------特定情况，可以考虑用ni.getName判断
					// 遍历所有ip
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						ip = (InetAddress) ips.nextElement();
						//log.info(ip.getHostAddress());
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.开头的都是lookback地址
								&& ip.getHostAddress().indexOf(":") == -1 && ((ip.getHostAddress().startsWith("192.168")
								|| ip.getHostAddress().startsWith("172.") || ip.getHostAddress().startsWith("10.")))
								&& !ip.getHostAddress().endsWith(".0.1")) {
							innerIpList.add(ip);
							//log.info("ip= {}" ,ip);
						}
					}

				}
			}
		} catch (Exception e) {
			log.error("get local ip error:", e);
		}
		if (innerIpList.size() > 0) {
			sIP = innerIpList.get(0).getHostAddress();
		}
		return sIP;
	}


	
}
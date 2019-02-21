package com.di.kit;

public class IpUtil {
	public static int ipToInt(String ip) {
		String[] ips = ip.split(".");
		int ipFour = 0;
		for (String ip4 : ips) {
			Integer ip4a = Integer.parseInt(ip4);
			ipFour = (ipFour << 8) | ip4a;
		}
		return ipFour;
	}

	public static String intToIp(Integer ip) {
		StringBuilder sb = new StringBuilder();
		for (int i = 3; i >= 0; i--) {
			int ipa = (ip >> (8 * i)) & (0xff);
			sb.append(ipa + ".");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}
}

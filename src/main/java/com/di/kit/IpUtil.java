package com.di.kit;

public class IpUtil {
	public static long ipToLong(String ip) {
		String[] ips = ip.split("\\.");
		long ipFour = 0;
		for (String ip4 : ips) {
			Long ip4a = Long.parseLong(ip4);
			ipFour = (ipFour << 8) | ip4a;
		}
		return ipFour;
	}

	public static String longToIp(long ip) {
		StringBuilder sb = new StringBuilder();
		for (int i = 3; i >= 0; i--) {
			long ipa = (ip >> (8 * i)) & (0xff);
			sb.append(ipa + ".");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}
}

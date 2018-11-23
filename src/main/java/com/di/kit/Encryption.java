package com.di.kit;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * @author d
 */
public class Encryption {

	public static String base64Decode(String input) {
		return new String(Base64.getDecoder().decode(input));
	}

	public static String base64Encode(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}

	public static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String sha(String shakey, String inStr) {
		try {
			MessageDigest sha = MessageDigest.getInstance(shakey);
			byte[] byteArray = inStr.getBytes("UTF-8");
			byte[] md5Bytes = sha.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String sha256(String s) {
		return sha("SHA-256", s);
	}

	public static String sha512(String s) {
		return sha("SHA-512", s);
	}
}

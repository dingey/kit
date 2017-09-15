package com.di.kit;

import java.security.MessageDigest;

/**
 * @author d
 */
public class EncryptionUtil {
	private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	/**
	 * base64解密
	 * 
	 * @param input 输入字符串
	 * @return base64编码后的字符串
	 */
	public static String base64DecodeString(String input) {
		return new String(base64Decode(input));
	}

	/**
	 * base64解密
	 * 
	 * @param input 输入字符串
	 * @return base64解码后的字节
	 */
	public static byte[] base64Decode(String input) {
		if (input.length() % 4 != 0) {
			throw new IllegalArgumentException("Invalid base64 input");
		}
		byte decoded[] = new byte[((input.length() * 3) / 4)
				- (input.indexOf('=') > 0 ? (input.length() - input.indexOf('=')) : 0)];
		char[] inChars = input.toCharArray();
		int j = 0;
		int b[] = new int[4];
		for (int i = 0; i < inChars.length; i += 4) {
			// This could be made faster (but more complicated) by precomputing
			// these index locations.
			b[0] = CODES.indexOf(inChars[i]);
			b[1] = CODES.indexOf(inChars[i + 1]);
			b[2] = CODES.indexOf(inChars[i + 2]);
			b[3] = CODES.indexOf(inChars[i + 3]);
			decoded[j++] = (byte) ((b[0] << 2) | (b[1] >> 4));
			if (b[2] < 64) {
				decoded[j++] = (byte) ((b[1] << 4) | (b[2] >> 2));
				if (b[3] < 64) {
					decoded[j++] = (byte) ((b[2] << 6) | b[3]);
				}
			}
		}
		return decoded;
	}

	/**
	 * base64加密
	 * 
	 * @param s 输入字符串
	 * @return base64解码后字符串
	 */
	public static String base64Encode(String s) {
		return base64Encode(s.getBytes());
	}

	/**
	 * base64加密
	 * 
	 * @param in 输入字节
	 * @return 输出base64加密字符串
	 */
	public static String base64Encode(byte[] in) {
		StringBuilder out = new StringBuilder((in.length * 4) / 3);
		int b;
		for (int i = 0; i < in.length; i += 3) {
			b = (in[i] & 0xFC) >> 2;
			out.append(CODES.charAt(b));
			b = (in[i] & 0x03) << 4;
			if (i + 1 < in.length) {
				b |= (in[i + 1] & 0xF0) >> 4;
				out.append(CODES.charAt(b));
				b = (in[i + 1] & 0x0F) << 2;
				if (i + 2 < in.length) {
					b |= (in[i + 2] & 0xC0) >> 6;
					out.append(CODES.charAt(b));
					b = in[i + 2] & 0x3F;
					out.append(CODES.charAt(b));
				} else {
					out.append(CODES.charAt(b));
					out.append('=');
				}
			} else {
				out.append(CODES.charAt(b));
				out.append("==");
			}
		}
		return out.toString();
	}

	/**
	 * md5加密
	 * 
	 * @param s 代价密的字符串
	 * @return md5加密后的字符串
	 */
	public String md5(String s) {
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
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * SHA加密 生成40位SHA码
	 * 
	 * @param inStr 待加密字符串
	 * @return 返回40位SHA码
	 */
	public static String shaEncode(String inStr) throws Exception {
		MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance("SHA");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
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
	}

}

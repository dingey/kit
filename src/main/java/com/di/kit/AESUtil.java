package com.di.kit;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	// 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
	private static String sKey = "&a^g#f@b%apz10wm";
	private static String ivParameter = "0123456789abcdef";

	public static String encrypt(String sSrc) {
		return encrypt(sSrc, sKey, ivParameter);
	}

	// 加密
	public static String encrypt(String sSrc, String skey, String ivp) {
		String result = "";
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] raw = skey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			IvParameterSpec iv = new IvParameterSpec(ivp.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
			result = new String(Base64.getEncoder().encode(encrypted), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String decrypt(String sSrc) {
		return decrypt(sSrc, sKey, ivParameter);
	}

	// 解密
	public static String decrypt(String sSrc, String skey, String ivp) {
		try {
			byte[] raw = skey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(ivp.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64.getDecoder().decode(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		// 需要加密的字串
		String cSrc = "测试";
		System.out.println(cSrc + "  长度为" + cSrc.length());
		// 加密
		long lStart = System.currentTimeMillis();
		String enString = encrypt(cSrc);
		System.out.println("加密后的字串是：" + enString + "长度为" + enString.length());

		long lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("加密耗时：" + lUseTime + "毫秒");
		// 解密
		lStart = System.currentTimeMillis();
		String DeString = decrypt(enString);
		System.out.println("解密后的字串是：" + DeString);
		lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("解密耗时：" + lUseTime + "毫秒");
	}
}

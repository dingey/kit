package com.di.kit;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author d
 */
public class AesUtil {
    /**
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private static final String KEY = "&a^g#f@b%apz10wm";
    private static final String IV_PARAMETER = "0123456789abcdef";

    private AesUtil() {
    }

    /**
     * 加密
     *
     * @param sSrc 明文
     * @return 密文
     */
    public static String encrypt(String sSrc) {
        return encrypt(sSrc, KEY, IV_PARAMETER);
    }

    /**
     * 加密
     *
     * @param sSrc 明文
     * @param skey 密钥，长度必须是8的倍数
     * @param ivp  加密向量
     * @return 密文
     */
    public static String encrypt(String sSrc, String skey, String ivp) {
        try {
            Cipher cipher;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = skey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivp.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return new String(Base64.getEncoder().encode(encrypted), "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param sSrc 密文
     * @return 明文
     */
    public static String decrypt(String sSrc) {
        return decrypt(sSrc, KEY, IV_PARAMETER);
    }

    /**
     * 解密
     *
     * @param sSrc 密文
     * @param skey 密钥，长度必须是8的倍数
     * @param ivp  加密向量
     * @return 明文
     */
    public static String decrypt(String sSrc, String skey, String ivp) {
        try {
            byte[] raw = skey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivp.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        String decrypt = decrypt(enString);
        System.out.println("解密后的字串是：" + decrypt);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }
}

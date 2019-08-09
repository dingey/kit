package com.di.kit;

import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author di
 */
public class DesUtil {
    /**
     * 密钥，是加密解密的凭据，长度为8的倍数
     */
    private static final String PASSWORD_CRYPT_KEY = "1a*fjo@$";
    private static final String DES = "DES";

    private DesUtil() {
    }

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception 异常
     */
    private static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception 异常
     */
    private static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    /**
     * 密码解密
     *
     * @param data 数据
     * @param key  秘钥
     * @return 加密后的数据
     */
    public static String decrypt(String data, String key) {
        try {
            return new String(decrypt(hex2byte(data.getBytes()), key.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String data) {
        return decrypt(data, PASSWORD_CRYPT_KEY);
    }

    /**
     * 密码加密
     *
     * @param password 密文
     * @param key      秘钥
     * @return 明文
     */
    public static String encrypt(String password, String key) {
        try {
            return byte2hex(encrypt(password.getBytes(), key.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String password) {
        return encrypt(password, PASSWORD_CRYPT_KEY);
    }

    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte aB : b) {
            stmp = (Integer.toHexString(aB & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public static void main(String[] args) {
        String basestr = "this is 我的 #$%^&()first encrypt program 知道吗?DES算法要求有一个可信任的随机数源 --//*。@@@1";
        long lStart = System.currentTimeMillis();
        String encrypt = encrypt(basestr);
        long lUseTime = System.currentTimeMillis() - lStart;

        System.out.println("原始值: " + basestr);
        System.out.println("加密后: " + encrypt);
        System.out.println("加密耗时：" + lUseTime + "毫秒");

        lStart = System.currentTimeMillis();
        String decrypt = decrypt(encrypt);
        System.out.println("解密后：" + decrypt);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }
}

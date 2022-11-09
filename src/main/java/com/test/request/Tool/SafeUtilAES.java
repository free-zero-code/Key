package com.test.request.Tool;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Add by (2022-10-21)[任建强]{AES加密与解密安全算法}
 */

public class SafeUtilAES {
    // 加密:
    public static String Encryption(String src_data,String password) throws GeneralSecurityException {
        // 创建一个MessageDigest实例:
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // 反复调用update输入数据:
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] key = md.digest();
        byte[] data = src_data.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encrypt(key, data);
        // 对比特流进行base64加密:
        return Base64.encodeBase64String(encrypted);
    }
    // 解密:
    public static String Decryption(String src_data,String password) throws GeneralSecurityException {
    	// 对base64密文进行解密:
        byte[] bytes = Base64.decodeBase64(src_data);
        // 创建一个MessageDigest实例:
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // 反复调用update输入数据:
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] key = md.digest();
        byte[] decrypted = decrypt(key,bytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    // 加密核心算法:
    private static byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        // CBC模式需要生成一个16 bytes的initialization vector:
        SecureRandom sr = new SecureRandom();
        byte[] iv = sr.generateSeed(16);
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);
        byte[] data = cipher.doFinal(input);
        // IV不需要保密，把IV和密文一起返回:
        return join(iv, data);
    }
    // 解密核心算法:
    private static byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        // 把input分割成IV和密文:
        byte[] iv = new byte[16];
        byte[] data = new byte[input.length - 16];
        System.arraycopy(input, 0, iv, 0, 16);
        System.arraycopy(input, 16, data, 0, data.length);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivps = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
        return cipher.doFinal(data);
    }
    // 拼接IV与密文
    private static byte[] join(byte[] bs1, byte[] bs2) {
        byte[] r = new byte[bs1.length + bs2.length];
        System.arraycopy(bs1, 0, r, 0, bs1.length);
        System.arraycopy(bs2, 0, r, bs1.length, bs2.length);
        return r;
    }
}

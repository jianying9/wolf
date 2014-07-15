package com.wolf.framework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 加密辅助类
 *
 * @author aladdin
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 用MD5加密
     *
     * @param str
     * @return
     */
    public static String encryptByMd5(String str) {
        String result = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            byte[] messageDigest = algorithm.digest(str.getBytes());
            result = byteToHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }

    /**
     * byte转16进制字符
     *
     * @param textByte
     * @return
     */
    public static String byteToHexString(byte[] textByte) {
        StringBuilder hexString = new StringBuilder(32);
        int byteValue;
        for (byte bt : textByte) {
            byteValue = 0xFF & bt;
            if (byteValue < 16) {
                hexString.append('0').append(Integer.toHexString(byteValue));
            } else {
                hexString.append(Integer.toHexString(byteValue));
            }
        }
        return hexString.toString();
    }

    /**
     * 16进制字符转byte
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToByte(String hexString) {
        byte[] result = new byte[hexString.length() / 2];
        String str;
        int byteValue;
        for (int index = 0; index < hexString.length(); index = index + 2) {
            str = hexString.substring(index, index + 2);
            byteValue = Integer.parseInt(str, 16);
            result[index / 2] = (byte) byteValue;
        }
        return result;
    }

    /**
     * des加密
     *
     * @param text
     * @param key
     * @return
     */
    public static byte[] encryptByDes(String text, String key) {
        byte[] resultByte = {};
        try {
            byte[] keyByte = key.getBytes();
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(keyByte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keyByte);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] textByte = text.getBytes();
            resultByte = cipher.doFinal(textByte);
        } catch (Exception e) {
        }
        return resultByte;
    }

    /**
     * des解密
     *
     * @param entryByte
     * @param key
     * @return
     */
    public static String decryptByDes(byte[] entryByte, String key) {
        String result = "";
        try {
            byte[] keyByte = key.getBytes();
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(keyByte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keyByte);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] resultByte = cipher.doFinal(entryByte);
            result = new String(resultByte);
        } catch (Exception e) {
        }
        return result;
    }
}

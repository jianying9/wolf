package com.wolf.framework.utils;

import java.io.UnsupportedEncodingException;
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

    private static String byteToHexString(byte[] textByte) {
        StringBuilder hexString = new StringBuilder(64);
        int byteValue;
        for (byte bt : textByte) {
            byteValue = 0xFF & bt;
            if (byteValue < 16) {
                hexString.append("0").append(Integer.toHexString(0xFF & bt));
            } else {
                hexString.append(Integer.toHexString(0xFF & bt));
            }
        }
        return hexString.toString();
    }

    /**
     * B64可逆加密
     *
     * @param source
     * @param key
     * @return
     */
    public static String commonEncrypt(String source, String key) {
        String result = "";
        String str = encryptByMd5("128");
        int startIndex = 0;
        int endIndex = 1;
        byte[] sourceByte = source.getBytes();
        byte[] destiationByte = new byte[sourceByte.length * 2];
        for (int index = 0; index < sourceByte.length; index++) {
            if (startIndex == str.length()) {
                startIndex = 0;
                endIndex = 1;
            }
            destiationByte[index * 2] = str.substring(startIndex, endIndex).getBytes()[0];
            destiationByte[index * 2 + 1] = (byte) (sourceByte[index] ^ destiationByte[index * 2]);
            startIndex++;
            endIndex++;
        }
        try {
            result = EncodingToBase64(keyED(destiationByte, key));
        } catch (UnsupportedEncodingException e) {
        }
        return result;
    }

    private static byte[] keyED(byte[] source, String key) throws UnsupportedEncodingException {
        String str = encryptByMd5(key);
        int startIndex = 0;
        int endIndex = 1;
        for (int index = 0; index < source.length; index++) {
            if (str.length() == startIndex) {
                startIndex = 0;
                endIndex = 1;
            }
            int num = source[index] ^ str.substring(startIndex, endIndex).getBytes()[0];
            source[index] = (byte) num;
            startIndex++;
            endIndex++;
        }
        return source;
    }

    private static String EncodingToBase64(byte[] str) {
        return new sun.misc.BASE64Encoder().encode(str);
    }

    public static String encryptByDesGb2312(String text, String key) {
        String result = "";
        try {
            byte[] keyByte = key.getBytes("gb2312");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(keyByte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keyByte);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] textByte = text.getBytes("gb2312");
            byte[] resultByte = cipher.doFinal(textByte);
            result = byteToHexString(resultByte);
        } catch (Exception e) {
        }
        return result;
    }
}

package com.wolf;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class SecurytiJUnitTest {

    public SecurytiJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private String byteToHexString(byte[] textByte) {
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

    private byte[] hexStringToByte(String hexString) {
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

    @Test
    public void desTest2() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        String text = "Message";
        System.out.println(text);
        String key = "4102gnab";
        byte[] keyByte = key.getBytes();
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(keyByte);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(keyByte);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] textByte = text.getBytes();
        byte[] resultByte = cipher.doFinal(textByte);
        text = this.byteToHexString(resultByte);
        System.out.println(text);
        //
        resultByte = this.hexStringToByte(text);
        cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        desKeySpec = new DESKeySpec(keyByte);
        keyFactory = SecretKeyFactory.getInstance("DES");
        secretKey = keyFactory.generateSecret(desKeySpec);
        iv = new IvParameterSpec(keyByte);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        resultByte = cipher.doFinal(resultByte);
        text = new String(resultByte);
        System.out.println(text);
    }
}
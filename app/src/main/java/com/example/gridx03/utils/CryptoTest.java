package com.example.gridx03.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoTest {

    // source: http://www.inconteam.com/software-development/41-encryption/55-aes-test-vectors#aes-cbc-128


    public CryptoTest() {
    }

    public static byte[] hexStringToByteArray(String hexInputString) {
        byte[] bts = new byte[hexInputString.length() / 2];

        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hexInputString.substring(2 * i, 2 * i + 2), 16);
        }

        return bts;
    }

    public static String byteArrayToString(byte[] byteArray) {
        StringBuilder str = new StringBuilder();

        for (byte b : byteArray) {
            str.append((char) b);
        }

        return str.toString();
    }

    public static String byteArrayToHexString(byte[] arg) {
        int l = arg.length * 2;
        return String.format("%0" + l + "x", new BigInteger(1, arg));
    }

    public static byte[] encrypt(byte[] key1, byte[] key2, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2);
            SecretKeySpec skeySpec = new SecretKeySpec(key1, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value);

            return encrypted;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(byte[] key1, byte[] key2, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2);
            SecretKeySpec skeySpec = new SecretKeySpec(key1, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(encrypted);

            return original;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public static byte[] encryptECB(String strToEncrypt, String key) {
        try {
            byte[] keyBytes = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());

            return encrypted;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decryptECB(String strToDecrypt, String key) {
        try {
            byte[] DecodedHax = hexStringToByteArray(strToDecrypt);
            byte[] keyBytes = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/NOPADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] original = cipher.doFinal(DecodedHax);

            return new String(original);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "decryption failed";
    }

    public static SecretKeySpec setKey(String myKey) {
        byte[] key;
        SecretKeySpec secretKey;
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");

            return secretKey;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String toHex(String arg) {
        int l = arg.length() * 2;

        return String.format("%0" + l + "x", new BigInteger(1, arg.getBytes()));
    }

    public static String HexStringToString(String arg) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < arg.length(); i += 2) {
            String str = arg.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

}
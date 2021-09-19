package com.example.gridx03.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {
///https://stackoverflow.com/questions/38335994/aes-keys-different-in-arduino-and-android-implementation
    private static CryptoManager shared;
    private String privateKey = "your_private_key_here";
    private String ivString = "your_iv_here";

    byte aes_key[] = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x03};

    // General initialization vector (you must use your own IV's in production for full security!!!)
    byte aes_iv[] = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};


    public CryptoManager(){
    }



    public String encrypt(String plainText) {
        String key1 =    "0000000000000000";
        String iv1 =      "0000000000000000";
        try {
            IvParameterSpec iv = new IvParameterSpec(iv1.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes(), "AES");

            String base54PlainText= android.util.Base64.encodeToString(plainText.getBytes(), android.util.Base64.DEFAULT);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(base54PlainText.getBytes());
            return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Encrption falied";
    }


    public String decrypt(String encrypted) {
        String key1 =    "0000000000000000";
        String iv1 =      "0000000000000000";
        try {

            IvParameterSpec iv = new IvParameterSpec(iv1.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = new byte[0];
            original = cipher.doFinal(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT));
            String data = new String(original,StandardCharsets.UTF_8);

            return new String(android.util.Base64.decode(data, android.util.Base64.DEFAULT));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


}

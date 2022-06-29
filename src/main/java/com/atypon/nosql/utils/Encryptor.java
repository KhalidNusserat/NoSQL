package com.atypon.nosql.utils;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encryptor {

    private static final String algorithm = "AES/CBC/PKCS5Padding";

    private static final IvParameterSpec iv = generateIv();

    private static final String password = ">~`EK>W1+Ftr:c=!t7)Y>xhtyV1@`4sVB_H6^b}Z83";

    private static final String salt = "yTglZdEl5=0I)jmo";

    private static final SecretKey secretKey = getKeyFromPassword();

    private Encryptor() {
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    @SneakyThrows
    private static SecretKey getKeyFromPassword() {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    @SneakyThrows
    public static String encrypt(String input) {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    @SneakyThrows
    public static String decrypt(String cipherText) {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }
}
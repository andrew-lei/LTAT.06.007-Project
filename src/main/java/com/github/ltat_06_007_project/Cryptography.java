package com.github.ltat_06_007_project;

import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.nio.file.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Cryptography {
    public static void genKeyPair(String outFile) throws IOException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair kp = keyGen.generateKeyPair();
            byte[] publicKey = kp.getPublic().getEncoded();
            byte[] privateKey = kp.getPrivate().getEncoded();

            FileOutputStream out = new FileOutputStream(outFile + ".key");
            out.write(privateKey);
            out.close();

            out = new FileOutputStream(outFile + ".pub");
            out.write(publicKey);
            out.close();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey genAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static PublicKey readPub(String filepath) throws IOException {
        /* Read all bytes from the private key file*/
        Path path = Paths.get(filepath);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key.*/
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(ks);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readPubBytes(String filepath) throws IOException {
        return Files.readAllBytes(Paths.get(filepath));
    }

    public static PrivateKey readKey(String filepath) throws  IOException {
        /* Read all bytes from the private key file*/
        Path path = Paths.get(filepath);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key.*/
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(ks);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptSymKey(PublicKey pub, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pub);
        return cipher.doFinal(key.getEncoded());
    }

    public static SecretKey decryptSymKey(PrivateKey key, byte[] encKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decKey = cipher.doFinal(encKey);
        return new SecretKeySpec(decKey, 0, decKey.length, "AES");
    }

    public static byte[] encryptText(SecretKey key, byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(text);
    }

    public static byte[] decryptText(SecretKey key, byte[] encText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encText);
    }
}

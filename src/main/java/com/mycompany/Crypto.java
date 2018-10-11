package com.mycompany.crypto;

import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.nio.file.*;
import javax.crypto.*;

public class Crypto {
    public static void genKeyPair(String outFile) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair kp = keyGen.generateKeyPair();
        byte[] publicKey = kp.getPublic().getEncoded();
        byte[] privateKey = kp.getPrivate().getEncoded();
        StringBuffer retString = new StringBuffer();
        for (int i = 0; i < publicKey.length; ++i) {
            retString.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
        }
        StringBuffer retString2 = new StringBuffer();
        for (int i = 0; i < privateKey.length; ++i) {
            retString2.append(Integer.toHexString(0x0100 + (privateKey[i] & 0x00FF)).substring(1));
        }

        FileOutputStream out = new FileOutputStream(outFile + ".key");
        out.write(privateKey);
        out.close();

        out = new FileOutputStream(outFile + ".pub");
        out.write(publicKey);
        out.close();
    }

    public static PublicKey readPub(String filepath) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        /* Read all bytes from the private key file */
        Path path = Paths.get(filepath);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key. */
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }

    public static PrivateKey readKey(String filepath) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        /* Read all bytes from the private key file */
        Path path = Paths.get(filepath);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(ks);
    }
    public static byte[] encrypt(PublicKey pub, String text) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pub);
        return cipher.doFinal(text.getBytes("UTF8"));
    }


    public static String decrypt(PrivateKey key, byte[] enctext) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        String decText = new String(cipher.doFinal(enctext));
        return decText;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        genKeyPair("foobar");
        PrivateKey pvt = readKey("foobar.key");
    }
}

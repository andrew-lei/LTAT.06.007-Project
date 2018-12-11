package com.github.ltat_06_007_project;

import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.commons.io.IOUtils;
import org.digidoc4j.Configuration;
import org.digidoc4j.Configuration.Mode;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureBuilder;
import org.digidoc4j.signers.PKCS11SignatureToken;
import org.digidoc4j.X509Cert;
import org.digidoc4j.X509Cert.SubjectName;

public class Cryptography {

    private static Configuration configuration;

    static void init() {
        configuration = new Configuration(Mode.TEST);
        configuration.getTSL().refresh();
    }

    private static byte[] hashString(String password) {
        try {
            return MessageDigest.getInstance("MD5").digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static void generateKeyPair(String filepath, String password, char[] pin) throws IOException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair kp = keyGen.generateKeyPair();
            byte[] publicKey = kp.getPublic().getEncoded();
            byte[] privateKey = kp.getPrivate().getEncoded();

            SecretKey key = new SecretKeySpec(hashString(password), "AES");
            String privateKeyString = Cryptography.encryptText(Base64.getEncoder().encodeToString(privateKey), key,"pass");

            FileOutputStream out = new FileOutputStream(filepath + "/user.key");
            out.write(privateKeyString.getBytes());
            out.close();

            out = new FileOutputStream(filepath + "/user.pub");
            out.write(publicKey);
            out.close();


            Container container = ContainerBuilder.
                    aContainer().
                    withConfiguration(configuration).
                    withDataFile(filepath + "/user.pub", "text/plain").
                    build();

            PKCS11SignatureToken signatureToken = new PKCS11SignatureToken("C:/Windows/SysWOW64/onepin-opensc-pkcs11.dll", pin, 0);

            Signature signature = SignatureBuilder.
                    aSignature(container).
                    withSignatureToken(signatureToken).
                    invokeSigning();

            container.addSignature(signature);

            byte[] signedKey = Base64.getEncoder().encode(
                    IOUtils.toByteArray(
                            container.saveAsStream()));

            FileOutputStream outFile = new FileOutputStream(filepath + "/user.pub");
            outFile.write(signedKey);
            outFile.close();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static PrivateKey readPrivateKey(String filepath, String password) throws IOException {
        Path path = Paths.get(filepath + "/user.key");
        byte[] bytes = Files.readAllBytes(path);

        SecretKey key = new SecretKeySpec(hashString(password), "AES");
        try {
            byte[] privateKey = Base64.getDecoder().decode(Cryptography.decryptText(new String(bytes),key, "pass"));
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(ks);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] readPublicKeyContainer(String filepath) throws IOException {
        return Files.readAllBytes(Paths.get(filepath+"/user.pub"));
    }

    public static Container containerFromBytes(byte[] bytes) {
        return ContainerBuilder.
                aContainer().
                withConfiguration(configuration).
                fromStream(new ByteArrayInputStream(
                        Base64.getDecoder().decode(bytes))).
                build();
    }


    public static PublicKey getPublicKey(Container container) {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(container.getDataFiles().get(0).getBytes());
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(ks);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    static List<String> getSignerInfo(Container container) {
        List<String> retList = new ArrayList<>();
        Signature signature = container.getSignatures().get(0);
        X509Cert certificate = signature.getSigningCertificate();

        retList.add(certificate.getSubjectName(SubjectName.SERIALNUMBER));
        retList.add(certificate.getSubjectName(SubjectName.SURNAME));
        retList.add(certificate.getSubjectName(SubjectName.GIVENNAME));

        return retList;
    }

    public static boolean validateSignature(Container container) {
        return container.getSignatures().get(0).validateSignature().isValid();
    }

    public static SecretKey genAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(192);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptAESKey(SecretKey secretKey, PublicKey publicKey) {
        byte[] keyBytes = secretKey.getEncoded();
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(keyBytes));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey decryptAESKey(String cipherText, PrivateKey key) {
        byte[] encryptedKey = Base64.getDecoder().decode(cipherText);
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] keyBytes = cipher.doFinal(encryptedKey);
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException | InvalidKeyException| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String encryptText(String text, SecretKey key, String initVector) {
        try {
            byte[] textBytes = text.getBytes();
            byte[] nonce = hashString(initVector);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, nonce));
            return Base64.getEncoder().encodeToString(cipher.doFinal(textBytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptText(String cipherText, SecretKey key, String initVector) {
        try {
            byte[] ctextBytes = Base64.getDecoder().decode(cipherText.getBytes());
            byte[] nonce = hashString(initVector);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, nonce));
            return new String(cipher.doFinal(ctextBytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

}

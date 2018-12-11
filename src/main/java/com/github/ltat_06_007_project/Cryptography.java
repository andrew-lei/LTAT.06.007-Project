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
import org.digidoc4j.DataFile;
import org.digidoc4j.DataToSign;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureBuilder;
import org.digidoc4j.signers.PKCS11SignatureToken;
import org.digidoc4j.X509Cert;
import org.digidoc4j.X509Cert.SubjectName;

public class Cryptography {

    private static Configuration config;

    public static void init() {
        config = new Configuration(Mode.TEST);
        config.getTSL().refresh();
    }

    private static byte[] hashPass(String password) {
	try {
	    return MessageDigest.getInstance("MD5").digest(password.getBytes());
	} catch (NoSuchAlgorithmException e) {
	    throw new RuntimeException(e);
	}
    }

    public static void genKeyPair(String keyPath, String password, char[] pin) throws IOException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair kp = keyGen.generateKeyPair();
            byte[] publicKey = kp.getPublic().getEncoded();
            byte[] privateKey = kp.getPrivate().getEncoded();

            SecretKey key = new SecretKeySpec(hashPass(password), "AES");
            privateKey = Cryptography.encryptText(key, privateKey);

            FileOutputStream out = new FileOutputStream(keyPath + "/user.key");
            out.write(privateKey);
            out.close();

            out = new FileOutputStream(keyPath + "/user.pub");
            out.write(publicKey);
            out.close();


            Cryptography.signKey(keyPath + "/user.pub","C:/Windows/SysWOW64/onepin-opensc-pkcs11.dll",pin,keyPath + "/user.pub");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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

    public static PublicKey readPub(String filepath) throws IOException {
        /* Read all bytes from the private key file*/
        Path path = Paths.get(filepath);
        byte[] bytes = Files.readAllBytes(path);
        return keyFromBytes (bytes);
    }

    public static PublicKey keyFromBytes(byte[] bytes) {
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

    public static PrivateKey readPrivateKey(String filepath, String password) throws IOException {
        /* Read all bytes from the private key file*/
        Path path = Paths.get(filepath);
        byte[] bytes = Files.readAllBytes(path);

        SecretKey key = new SecretKeySpec(hashPass(password), "AES");
	try {
            byte[] privateKey = Cryptography.decryptText(key, bytes);
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(ks);
	} catch (BadPaddingException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
	    throw new RuntimeException(e);
	}
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
        return encryptBytes(pub, key.getEncoded());
    }

    public static byte[] encryptBytes(PublicKey pub, byte[] plainText) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, pub);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            return cipher.doFinal(plainText);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKey decryptSymKey(PrivateKey key, byte[] encKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decKey = cipher.doFinal(encKey);
        return new SecretKeySpec(decKey, 0, decKey.length, "AES");
    }

    public static byte[] decryptBytes(PrivateKey key, byte[] encryptedBytes){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            return cipher.doFinal(encryptedBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptText(SecretKey key, byte[] text) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();

        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptText(SecretKey key, byte[] encText) throws   InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
           return null;
        }
    }

    public static String encryptText(String text, SecretKey key, String initVector) {
        try {
	    byte[] textBytes = text.getBytes();
	    byte[] nonce = hashPass(initVector);

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
	    byte[] nonce = hashPass(initVector);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, nonce));
            return new String(cipher.doFinal(ctextBytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
           throw new RuntimeException(e);
        }
    }


    public static void signKey(String keyPath, String certPath, char[] password, String destPath) throws IOException {
        Container container = ContainerBuilder.
                aContainer().
                withConfiguration(config).
                withDataFile(keyPath, "text/plain").
                build();

        //Using the private key stored in the "signout.p12" file with password "test"
        PKCS11SignatureToken signatureToken = new PKCS11SignatureToken(certPath, password, 0);

        //Create a signature
        Signature signature = SignatureBuilder.
                aSignature(container).
                withSignatureToken(signatureToken).
                invokeSigning();

        //Add the signature to the container
        container.addSignature(signature);

        //Save the container as a .bdoc file
        byte[] signedKey = Base64.getEncoder().encode(
                IOUtils.toByteArray(
                        container.saveAsStream()));

        FileOutputStream outFile = new FileOutputStream(destPath);
        outFile.write(signedKey);
        outFile.close();
    }

    public static Container containerFromB64Bytes(byte[] input) {
        return ContainerBuilder.
                aContainer().
                withConfiguration(config).
                fromStream(new ByteArrayInputStream(
                        Base64.getDecoder().decode(input))).
                build();
    }

    public static Container containerFromBytes(byte[] input) {
        return ContainerBuilder.
                aContainer().
                withConfiguration(config).
                fromStream(new ByteArrayInputStream(input)).
                build();
    }

    public static byte[] getData(Container container) {
        return container.getDataFiles().get(0).getBytes();
    }

    public static PublicKey getPublicKey(Container container) {
        return keyFromBytes(getData(container));
    }

    public static boolean validateSignature(Container container) {
	return container.getSignatures().get(0).validateSignature().isValid();
    }

    public static List<String> getSignerInfo(Container container) {
        List<String> retList = new ArrayList<String>();
        Signature signature = container.getSignatures().get(0);
        X509Cert certificate = signature.getSigningCertificate();

        retList.add(certificate.getSubjectName(SubjectName.SERIALNUMBER));
        retList.add(certificate.getSubjectName(SubjectName.SURNAME));
        retList.add(certificate.getSubjectName(SubjectName.GIVENNAME));

        return retList;
    }
}

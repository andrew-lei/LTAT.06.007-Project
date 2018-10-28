package com.mycompany.crypto;

import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.ArrayList;
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

public class Crypto {
    public static void genKeyPair(String outFile) throws NoSuchAlgorithmException, IOException {
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
    }

    public static SecretKey genAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
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

    public static byte[] readPubBytes(String filepath) throws IOException {
        return Files.readAllBytes(Paths.get(filepath));
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

    public static void signKey(String keyPath, String certPath, char[] password, String destPath) throws IOException {
        Container container = ContainerBuilder.
            aContainer().
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
        Configuration config = new Configuration(Mode.TEST);
        return ContainerBuilder.
            aContainer().
            withConfiguration(config).
            fromStream(new ByteArrayInputStream(
                        Base64.getDecoder().decode(input))).
            build();
    }

    public static byte[] getData(Container container) {
        return container.getDataFiles().get(0).getBytes();
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

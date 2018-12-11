package com.github.ltat_06_007_project;

import org.digidoc4j.Container;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public interface CryptographyInterface {


    //Creates key pair,
    // signs public key with the EID signature,
    // encrypts private key with the password
    // writes both keys into separate files(as base64 string) on the filepath(key file names are hardcoded)
    // throws IOException if writing the files fails
    void generateKeyPair(String filepath, String password, char[] pin) throws IOException;


    //reads encrypted private key from file and decrypts it
    //private key file is located on the filepath, the file name is hardcoded
    //password is used do decrypt the file
    //Throws IOException if reading the file fails or the file is not a valid key
    //Throws ? when password is incorrect
    PrivateKey readPrivateKey(String filepath, String password) throws IOException;


    //reads signed public key bytes from file and returns the
    //Should verify whether the file is a valid signed public key, if not throw IOException
    //throws IOException if reading the file fails
    byte[] readPublicKeyContainer(String filepath) throws IOException;

    //Creates digidoc container from bytes(base64)(same as containerFromB64Bytes)
    //Throws ? if the bytes are not a vailid container
    Container containerFromBytes(byte[] bytes);

    //Same as before
    List<String> getSignerInfo(Container container);

    //gets the public key from the container
    //throws ? when file in container is not a valid public key
    PublicKey getPublicKey(Container container);


    //validates the signature of the container
    boolean validateSignature(Container container);


    //generates 256 byte AES key
    SecretKey genAESKey();


    //encypts secretKey with the given public key
    //encodes it as base64 string
    String encryptAESKey(SecretKey secretKey, PublicKey publicKey);

    //returns 256 byte AES key
    //cipherText is encrypted AES key encoded as a base 64 String
    //Throws ? when ciphertext is not a valid SecretKey
    SecretKey decryptAESKey(String cipherText, PrivateKey key);


    //encodedText - base
    //ecrypts text with the secret key in the AES-GCM mode
    String encryptText(String text, SecretKey secretKey, String initVector);

    //Decrypts the ciphertext
    //ciphertext is base64 encoded
    //throws ? when the when decryption fails
    String decrypt(String cipherText, SecretKey secretKey, String initVector);




}

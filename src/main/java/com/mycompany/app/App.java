package com.mycompany.app;

import org.digidoc4j.Configuration;
import org.digidoc4j.Configuration.Mode;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.DataFile;
import org.digidoc4j.DataToSign;
import org.digidoc4j.Signature;
import org.digidoc4j.SignatureBuilder;
import org.digidoc4j.signers.PKCS12SignatureToken;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;

import com.mycompany.crypto.Crypto;

public class App 
{
    public static void main( String[] args ) throws Exception //NoSuchAlgorithmException, IOException 
    {
        Crypto.genKeyPair("data/test");

        Configuration config = new Configuration(Mode.TEST);
        //Create a container with two text files to be signed
        Container container = ContainerBuilder.
            aContainer().
            withConfiguration(config).
            withDataFile("data/test.pub", "text/plain").
            build();

        //Using the private key stored in the "signout.p12" file with password "test"
        String privateKeyPath = "data/certificate.p12";
        char[] password = "test".toCharArray();
        PKCS12SignatureToken signatureToken = new PKCS12SignatureToken(privateKeyPath, password);
        System.out.println(signatureToken);
        //Create a signature
        Signature signature = SignatureBuilder.
            aSignature(container).
            withSignatureToken(signatureToken).
            invokeSigning();
        System.out.println(signature);
        //Add the signature to the container
        container.addSignature(signature);

        //Save the container as a .bdoc file
        container.saveAsFile("data/test-container.bdoc");

        PrivateKey key = Crypto.readKey("data/test.key");
        PublicKey pub = Crypto.readPub("data/test.pub");

        byte[] enctext = Crypto.encrypt(pub, "Hello world!");
        System.out.println(new String(enctext));
        System.out.println(Crypto.decrypt(key, enctext));

   }
}

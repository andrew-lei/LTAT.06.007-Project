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
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import javax.crypto.SecretKey;

import com.mycompany.crypto.Crypto;

public class App 
{
    public static void main( String[] args ) throws Exception //NoSuchAlgorithmException, IOException 
    {
        if (args.length > 0) {
            if (args[0].equals("genkey")) {
                Crypto.genKeyPair("data/test");

            }
            else if (args[0].equals("signkey")) {
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
            }
            else if (args[0].equals("encrypt")) {
                PublicKey pub = Crypto.readPub("data/test.pub");
                SecretKey key = Crypto.genAESKey();

                byte[] encKey = Crypto.encryptSymKey(pub, key);
                FileOutputStream out = new FileOutputStream("data/key.enc");
                out.write(encKey);
                out.close();

                byte[] encText = Crypto.encryptText(key, "Hello world!".getBytes("UTF8"));
                FileOutputStream out2 = new FileOutputStream("data/message.enc");
                out2.write(encText);
                out2.close();
            }
            else if (args[0].equals("decrypt")) {
                PrivateKey pvt = Crypto.readKey("data/test.key");
                byte[] encKey = Files.readAllBytes(Paths.get("data/key.enc"));
                byte[] encText = Files.readAllBytes(Paths.get("data/message.enc"));

                SecretKey key = Crypto.decryptSymKey(pvt, encKey);
                byte[] decText = Crypto.decryptText(key, encText);
                
                System.out.println(new String(decText));
            }
        }
   }
}

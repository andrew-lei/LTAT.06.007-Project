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

import org.digidoc4j.X509Cert.SubjectName;

import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Scanner;
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
                System.out.println("PIN:");
                Scanner scanner = new Scanner(System.in);
                String pin = scanner.nextLine();
                Crypto.signKey("data/test.pub", "data/certificate.p12", pin.toCharArray(), "data/signed.pub");
            }
            else if (args[0].equals("readsignedpub")) {
                Container container = Crypto.containerFromB64Bytes(Files.readAllBytes(Paths.get("data/signed.pub")));
                System.out.println(new String(Crypto.getData(container)));

                Signature signature = container.getSignatures().get(0);
                System.out.println(signature.getSigningCertificate().getSubjectName(SubjectName.SURNAME));
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

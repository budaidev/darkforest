package com.pm.mentor.darkforest.util;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

public class RSAKeyPairGenerator {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {

        // Kulcspár generálása
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA_SIGNATURE");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.genKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // A publikus kulcsot PEM formátumra alakítjuk
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(publicKey);
        } catch (IOException e) {
            // Kezeljük az esetleges hibát
        }
        String publicKeyPEM = stringWriter.toString();

        System.out.println("Without encoding:");
        System.out.println("Private Key: " + privateKey);
        System.out.println("Public Key (PEM): " + publicKeyPEM);
        System.out.println("Public Key: " + publicKey.toString());


        // A kulcsokat Base64 kódoljuk és kiírjuk a konzolra
        Base64 base64 = new Base64();
        String privateKeyString = base64.encodeToString(privateKey.getEncoded());
        String publicKeyString = base64.encodeToString(publicKeyPEM.getBytes());
        String publicKeySimple = base64.encodeToString(publicKey.getEncoded());

        System.out.println("Private Key: " + privateKeyString);
        System.out.println("Public Key (PEM): " + publicKeyString);
        System.out.println("Public Key: " + publicKeySimple);

    }
}

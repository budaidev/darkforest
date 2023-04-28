package com.pm.mentor.darkforest.util;

import com.pm.mentor.darkforest.config.ClientConfiguration;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class Signer {

    private final ClientConfiguration clientConfiguration;

    public Signer(ClientConfiguration clientConfiguration){
        this.clientConfiguration = clientConfiguration;
    }
	
	@SneakyThrows
	public Signed sign() {
        long ts = System.currentTimeMillis();

        // Generate key pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        PrivateKey privateKey = getPrivateKey();

        // Generate signature
        String signatureString = "email=" + URLEncoder.encode(clientConfiguration.getEmail(), "UTF-8")
                + "&team=" + URLEncoder.encode(clientConfiguration.getTeam(), "UTF-8")
                + "&ts=" + ts;
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(signatureString.getBytes("UTF-8"));
        byte[] signatureBytes = signature.sign();

        // Encode signature in Base64 and URL-encode it
        String encodedSignature = URLEncoder.encode(Base64.getEncoder().encodeToString(signatureBytes), "UTF-8");
        
        return new Signed(ts, encodedSignature);
	}
	
	public PrivateKey getPrivateKey() throws IOException {
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"+ clientConfiguration.getSignature() +"\n-----END PRIVATE KEY-----";
        PEMParser pemParser = new PEMParser(new StringReader(privateKeyPem));
        Object privateKeyObj = pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        return converter.getPrivateKey((PrivateKeyInfo) privateKeyObj);
    }
	
	@AllArgsConstructor
	public static class Signed {
		@Getter
		private long timeStamp;
		
		@Getter
		private String signature;
	}
}

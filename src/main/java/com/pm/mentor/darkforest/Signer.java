package com.pm.mentor.darkforest;

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

public class Signer {
	
	public static final String Email = "budai.attila.istvan@gmail.com";
	public static final String Team = "PM Mentor team";
	
	public static Signed sign() {
		try {
	        long ts = System.currentTimeMillis();
	
	        // Generate key pair
	        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	        kpg.initialize(2048);
	        PrivateKey privateKey = getPrivateKey();
	
	        // Generate signature
	        String signatureString = "email=" + URLEncoder.encode(Email, "UTF-8")
	                + "&team=" + URLEncoder.encode(Team, "UTF-8")
	                + "&ts=" + ts;
	        Signature signature = Signature.getInstance("SHA256withRSA");
	        signature.initSign(privateKey);
	        signature.update(signatureString.getBytes("UTF-8"));
	        byte[] signatureBytes = signature.sign();
	
	        // Encode signature in Base64 and URL-encode it
	        String encodedSignature = URLEncoder.encode(Base64.getEncoder().encodeToString(signatureBytes), "UTF-8");
	        
	        return new Signed(ts, encodedSignature);
		} catch (Exception e) {
			throw new DarkForestException(e);
		}
	}
	
	public static PrivateKey getPrivateKey() throws IOException {
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQClkyRdfeRtzJ23rEcdTGxgfw7i7iszjMhKPRLHAkdmeKULQH6RoOAgUlORkZywL/Wp8ZOoEIdzO738GBsIAgETCXv5xN9eOYEzPXdDxPZrsNNhxbWaf6Ei4bUmrlLE/0gtKUW70c8JlsJpvcf6HuD5z1ZdsmZ/ux9KX9AQq0e08NV6+m8h+C4aFwzf+z2ygLUdHEd4W46dGrNBOwDEqhfA0GbOao9FcZR3Zn6j6jAGjK3+0sMefbUDGlQldqRKehsHvsFOL1yHFZMvU9wV3cxDeWEJSyL8e6TEAIaxxbbNSR9gmeNVp669x00BruUKSV/c06LvRUebF63LTznGuxgbAgMBAAECggEAGpblHaB0eQ+FenQPnYxi0SJGZToDNQ2UMpvZMp1Skwmn04VBbiiMJZSi9/sO0xp4lJiHclNpNR+wdFX4rVIJEyIQRz2O82Y/sddamkb8nbGxWH+0GMILQftGKJaREzK9yAbbPHzGh08vHGUOQsQHtAZQY86c+EE4Fq0JUEv5QXyzDxzq00v2aJu10zpvU1oTpyGv0Uqec0PGCNwfEDr45yxzuQQgRnSj2HQYibsBU7WGJs4FJX+iRxeYGlz6Avz035YCO9e03FF+hPFnN5LiJy7E7vySWhoM5R4M5ExqgdxpT7xHAuKhS5V7T41wFX1P2fR4FTPC+B3CDc86Z01RFQKBgQDjgrW1bi8L5ozD85ysD3CMqg5WiXl2VtpgTaQJ9pglP+nS4Kh3/PqXq6o0yXWO9XE8EsgMJDRtfuKqV3a5/nc9o4ugbGZ5jRGiiAldfneVr8BHwxebTzvFrrtE4lmsyXaDGLbTVlglk85lQDPMgxDQSHj92KJleAepDRj+e9ndFwKBgQC6TvXhSOLQoN/iIXmXffu2oaa/OifseCi45zRz9ODk8HFcsHPky9RlsRDBJLdflLRpPhIu9+6g5wNl5H9jo5da8JC7el8xa+q+f56/5wWnHH/4dGSPME+GLI/xvRkyFRIOUjwYX39N2zpml+3XhggWK8OwIKAIgugS9Xg0sBAnnQKBgCXcKJTqoxWd5irtrVLMtvQRkJVCB150EZ8ZDIVD7gm02xpmnGJrTOBUhtyF8fQ6T/+pOHUcyUS3asziBTXqbLlrL98gauUrXpXngXd3hjr4pkzK4HDN/Kpm628JI9cnJ3ulbzc4FS8bq6sZxgTwgqnGhavokw3DrbKqJVCkF5s9AoGBAKBqjoG9DTbP3hXJ9vT+z/ZPt9CdlXShfYa/5MUXC3F0qldBw3g0HRy2WhPDtnSBCNo5TNTOO9SlNKLOHPXBDPiLfENAuahHIGeIYTshAxgBjRpztYYo9cA0rHhb9s7Nn69fFUlzKJzz75QYqvzQ9jOI2UTUwpaxiHuSij7rfe1tAoGAbbasmEcNY+GkFKrTYVTYTQWdATPbSB+5H1mk4CEfcegUeh/MsR6v8hoj2yf8YQlAnAl0M0oWa3gef2glC9fH79k0Vy/Rl0v8Hodw03mDAPZlN2nH4q9Eb8kDMGseXuc5hwTSL9jq/8RlKn8n2DT7AtvoFnvHBmkgpyN6GGA67go=\n-----END PRIVATE KEY-----";
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

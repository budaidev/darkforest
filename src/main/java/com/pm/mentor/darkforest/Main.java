package com.pm.mentor.darkforest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.val;

public class Main {
    public static void main(String[] args) throws Exception {
    	val sign = Signer.sign();

        // Make GET request
        String url = "http://javachallenge.loxon.eu:8081/game_api/getGameKey"
                + "?email=" + URLEncoder.encode(Signer.Email, StandardCharsets.UTF_8)
                + "&team=" + URLEncoder.encode(Signer.Team, StandardCharsets.UTF_8)
                + "&ts=" + sign.getTimeStamp()
                + "&signature=" + sign.getSignature();

        System.out.println(url);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // Read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        System.out.println(response.toString());
    }
}

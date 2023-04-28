package com.pm.mentor.darkforest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "client")
@Data
public class ClientConfiguration {

    private String url;
    private String port;
    private String email;
    private String team;
    private String signature;
}

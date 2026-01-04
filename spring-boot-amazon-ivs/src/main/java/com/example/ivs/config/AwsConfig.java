package com.example.ivs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ivs.IvsClient;
import software.amazon.awssdk.services.ivsrealtime.IvsRealtimeClient;

@Configuration
public class AwsConfig {

    @Value("${aws.ivs.access-key}")
    private String accessKey;

    @Value("${aws.ivs.secret-key}")
    private String secretKey;

    @Value("${aws.ivs.region}")
    private String region;

    @Bean
    public IvsClient ivsClient() {
        return IvsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    public IvsRealtimeClient ivsRealtimeClient() {
        return IvsRealtimeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
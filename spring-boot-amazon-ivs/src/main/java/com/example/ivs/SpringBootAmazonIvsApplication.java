package com.example.ivs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBootAmazonIvsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAmazonIvsApplication.class, args);
    }
}
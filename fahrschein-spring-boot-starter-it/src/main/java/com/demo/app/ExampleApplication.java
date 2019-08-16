package com.demo.app;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.fahrschein.AccessTokenProvider;

@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean("fahrscheinAccessTokenProvider")
    public AccessTokenProvider accessTokenProvider() {
        return new AccessTokenProvider() {

            @Override
            public String getAccessToken() throws IOException {
                return "NO_ACCESS_PLEASE";
            }
        };
    }
}

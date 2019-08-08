package com.demo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.stups.tokens.AccessToken;
import org.zalando.stups.tokens.AccessTokenUnavailableException;
import org.zalando.stups.tokens.AccessTokens;

@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean
    public AccessTokens accessTokens() {
        return new AccessTokens() {

            @Override
            public void stop() {
            }

            @Override
            public void invalidate(Object tokenId) {
            }

            @Override
            public AccessToken getAccessToken(Object tokenId) throws AccessTokenUnavailableException {
                return null;
            }

            @Override
            public String get(Object tokenId) throws AccessTokenUnavailableException {
                return tokenId.toString();
            }
        };
    }
}

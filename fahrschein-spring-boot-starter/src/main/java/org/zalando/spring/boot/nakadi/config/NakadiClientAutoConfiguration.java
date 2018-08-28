package org.zalando.spring.boot.nakadi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.stups.tokens.AccessTokens;

@Configuration
public class NakadiClientAutoConfiguration {

    @Bean
    public static NakadiClientsPostProcessor nakadiClientPostProcessor(AccessTokens accessTokens) {
        return new NakadiClientsPostProcessor(accessTokens);
    }

}

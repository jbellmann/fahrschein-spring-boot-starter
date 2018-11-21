package org.zalando.spring.boot.nakadi.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.stups.tokens.AccessTokens;

@Configuration
@AutoConfigureAfter(name = {
        "org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration",
        "org.zalando.logbook.spring.LogbookAutoConfiguration"
})
public class NakadiClientAutoConfiguration {

    @Bean
    public static NakadiClientsPostProcessor nakadiClientPostProcessor(AccessTokens accessTokens) {
        return new NakadiClientsPostProcessor(accessTokens);
    }

}

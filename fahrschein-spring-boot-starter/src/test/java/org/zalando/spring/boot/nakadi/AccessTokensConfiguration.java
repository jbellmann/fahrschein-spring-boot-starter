package org.zalando.spring.boot.nakadi;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.stups.tokens.AccessTokens;

@Configuration
public class AccessTokensConfiguration {

    @Bean
    public static AccessTokens accessTokens() {
        AccessTokens mocked = Mockito.mock(AccessTokens.class);
        Mockito.when(mocked.get(Mockito.anyString())).thenReturn("NO_REAL_TOKEN");
        return mocked;
    }
}

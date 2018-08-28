package org.zalando.spring.boot.nakadi.config;

import org.springframework.core.env.ConfigurableEnvironment;

public interface SettingsParser {

    boolean isApplicable();

    NakadiClientsProperties parse(ConfigurableEnvironment environment);

}

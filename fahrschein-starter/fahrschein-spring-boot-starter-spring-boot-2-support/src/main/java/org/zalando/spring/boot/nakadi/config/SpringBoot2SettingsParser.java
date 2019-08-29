package org.zalando.spring.boot.nakadi.config;

import static org.springframework.boot.context.properties.source.ConfigurationPropertySources.from;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

public class SpringBoot2SettingsParser implements SettingsParser {

    @Override
    public boolean isApplicable() {
        return ClassUtils.isPresent("org.springframework.boot.context.properties.bind.Binder",
                SpringBoot2SettingsParser.class.getClassLoader());
    }

    @Override
    public FahrscheinConfigProperties parse(ConfigurableEnvironment environment) {
        final Iterable<ConfigurationPropertySource> sources = from(environment.getPropertySources());
        final Binder binder = new Binder(sources);

        return binder.bind("fahrschein", FahrscheinConfigProperties.class)
                        .orElseCreate(FahrscheinConfigProperties.class);
    }

}

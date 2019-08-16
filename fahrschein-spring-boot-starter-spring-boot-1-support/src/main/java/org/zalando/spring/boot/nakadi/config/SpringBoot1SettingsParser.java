package org.zalando.spring.boot.nakadi.config;

import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

import lombok.SneakyThrows;

public class SpringBoot1SettingsParser implements SettingsParser {

    @Override
    public boolean isApplicable() {
        return ClassUtils.isPresent("org.springframework.boot.bind.PropertiesConfigurationFactory",
                getClass().getClassLoader());
    }

    @Override
    @SneakyThrows
    public FahrscheinConfigProperties parse(ConfigurableEnvironment environment) {

        final PropertiesConfigurationFactory<FahrscheinConfigProperties> factory =
                new PropertiesConfigurationFactory<>(FahrscheinConfigProperties.class);

        factory.setTargetName("fahrschein");
        factory.setPropertySources(environment.getPropertySources());
        factory.setConversionService(environment.getConversionService());

        return factory.getObject();
    }

}

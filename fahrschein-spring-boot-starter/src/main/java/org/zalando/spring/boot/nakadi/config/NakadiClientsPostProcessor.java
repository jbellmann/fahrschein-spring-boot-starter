package org.zalando.spring.boot.nakadi.config;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.zalando.spring.boot.config.Registry;
import org.zalando.stups.tokens.AccessTokens;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NakadiClientsPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private final AccessTokens accessTokens;

    private NakadiClientsProperties properties;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        final Collection<SettingsParser> parsers = Lists.newArrayList(ServiceLoader.load(SettingsParser.class));
        this.properties = parse((ConfigurableEnvironment) environment, parsers);
    }

    // visible for testing
    NakadiClientsProperties parse(final ConfigurableEnvironment environment, final Collection<SettingsParser> parsers) {
        final SettingsParser parser = parsers.stream()
                .filter(SettingsParser::isApplicable)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No applicable nakadi-clients settings parser available"));

        return (NakadiClientsProperties) parser.parse(environment);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        final NakadiClientsRegistrar registrar = new DefaultNakadiClientsRegistrar(new Registry(registry), properties, accessTokens, environment);
        registrar.register();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // nothing to do
    }
}

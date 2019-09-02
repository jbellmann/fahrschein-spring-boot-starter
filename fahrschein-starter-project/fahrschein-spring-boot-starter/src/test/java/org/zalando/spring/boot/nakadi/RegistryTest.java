package org.zalando.spring.boot.nakadi;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.zalando.fahrschein.NakadiClient;
import org.zalando.spring.boot.config.Registry;

public class RegistryTest {

    @Test
    public void testgeneratedBeanName() {
        String beanName = Registry.generateBeanName("first", NakadiClient.class);
        Assertions.assertThat(beanName).isEqualTo("firstNakadiClient");
    }

}

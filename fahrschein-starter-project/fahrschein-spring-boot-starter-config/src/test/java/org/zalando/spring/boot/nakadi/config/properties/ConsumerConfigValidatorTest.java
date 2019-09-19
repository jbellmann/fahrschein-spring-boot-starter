package org.zalando.spring.boot.nakadi.config.properties;

import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.zalando.spring.boot.nakadi.config.properties.ConsumerConfig;
import org.zalando.spring.boot.nakadi.config.properties.ConsumerConfigValidator;

public class ConsumerConfigValidatorTest {

    @Test
    public void testValidator() {
        ConsumerConfig cc = new ConsumerConfig();
        Errors errors = new BeanPropertyBindingResult(cc, "first");
        ValidationUtils.invokeValidator(new ConsumerConfigValidator(), cc, errors);
        System.out.println("HAS ERRORS : " + errors.hasErrors());
        System.out.println(errors.toString());
    }

}
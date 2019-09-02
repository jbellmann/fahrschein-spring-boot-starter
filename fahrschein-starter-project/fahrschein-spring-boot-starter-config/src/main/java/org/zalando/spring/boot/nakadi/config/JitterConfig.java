package org.zalando.spring.boot.nakadi.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JitterConfig {

    private Boolean enabled = Boolean.FALSE;

    private JitterType type = JitterType.EQUAL;
}

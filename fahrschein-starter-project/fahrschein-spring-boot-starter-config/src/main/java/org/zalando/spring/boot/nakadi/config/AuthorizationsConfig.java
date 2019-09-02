package org.zalando.spring.boot.nakadi.config;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class AuthorizationsConfig {

    private Map<String, String> admins = new LinkedHashMap<>();

    private Map<String, String> readers = new LinkedHashMap<>();

    private Boolean anyReader = Boolean.FALSE;

    public void mergeFromDefaults(AuthorizationsConfig defaultConfig) {

        if (this.admins.isEmpty()) {
            this.admins.putAll(defaultConfig.getAdmins());
        }

        if (this.readers.isEmpty()) {
            this.readers.putAll(defaultConfig.getReaders());
        }

        if (defaultConfig.getAnyReader() || this.getAnyReader()) {
            this.setAnyReader(Boolean.TRUE);
        }
    }

}

package org.zalando.spring.boot.nakadi.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

@Configuration
public class NakadiListenerSupportConfig {

    public static final String NAKADI_LISTENER_EXECUTOR_SERVICE = "nakadiListenerContainerExecutorService";

    @Bean(name = NAKADI_LISTENER_EXECUTOR_SERVICE)
    public ThreadPoolExecutorFactoryBean nakadiListenerContainerExecutorService() {
        ThreadPoolExecutorFactoryBean fb = new ThreadPoolExecutorFactoryBean();
        fb.setCorePoolSize(2);
        fb.setAllowCoreThreadTimeOut(true);
        fb.setMaxPoolSize(50);
        fb.setQueueCapacity(-1);
        fb.setThreadNamePrefix("nakadi-listener-");
        fb.setDaemon(true);

        return fb;
    }
}

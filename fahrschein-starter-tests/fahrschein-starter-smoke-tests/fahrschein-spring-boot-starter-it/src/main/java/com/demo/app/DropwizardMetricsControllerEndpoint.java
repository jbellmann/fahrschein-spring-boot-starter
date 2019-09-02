package com.demo.app;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@WebEndpoint(id = "dropwizard")
@RequiredArgsConstructor
public class DropwizardMetricsControllerEndpoint {

    @NonNull
    private final MetricRegistry registry;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false));
    }

    @ReadOperation(produces = MediaType.APPLICATION_JSON_VALUE)
    public String greet() {
        Writer writer = new StringWriter();
        try {
            getWriter(true).writeValue(writer, registry);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected ObjectWriter getWriter(boolean prettyPrint) {
        if (prettyPrint) {
            return mapper.writerWithDefaultPrettyPrinter();
        }
        return mapper.writer();
    }
}

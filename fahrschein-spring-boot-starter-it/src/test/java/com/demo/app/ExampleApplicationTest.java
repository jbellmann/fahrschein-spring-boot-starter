package com.demo.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.zalando.fahrschein.domain.DataOperation;
import org.zalando.spring.boot.nakadi.NakadiConsumer;
import org.zalando.spring.boot.nakadi.NakadiPublisher;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "spring.profiles.active=integrationTest",
        "context.initializer.classes=com.demo.app.ExampleApplicationTest.IntegrationTestInitializer"})
//@TestInstance(Lifecycle.PER_CLASS)
public class ExampleApplicationTest {

    private static final int NAKADI_UI_PORT = 3000;
    private static final String NAKADI_UI = "nakadi-ui";
    private static final int NAKADI_PORT = 8080;
    private static final String NAKADI = "nakadi";

    private static NakadiEnvironmentContainer nakadiEnv;
    private static String nakadiUrl;

    private static final RestOperations http = new RestTemplate();

    @Autowired
    @Qualifier("example")
    private NakadiConsumer outfitUpdateConsumer;

    @Autowired
    @Qualifier("outfitUpdatePublisher")
    private NakadiPublisher outfitUpdatePublisher;

    @BeforeAll
    public static void setup() throws IOException {
        nakadiEnv = new NakadiEnvironmentContainer()
                .withExposedService(NAKADI, NAKADI_PORT, Wait.forHttp("/event-types").forStatusCode(200))
                .withExposedService(NAKADI_UI, NAKADI_UI_PORT);
        nakadiEnv.start();

        initializeNakadiUrl();
        initializeNakadiEventTypes();
    }

    protected static void initializeNakadiUrl() {
        String nakadiHost = nakadiEnv.getServiceHost(NAKADI, NAKADI_PORT);
        int nakadiPort = nakadiEnv.getServicePort(NAKADI, NAKADI_PORT);
        nakadiUrl = String.format("http://%s:%s", nakadiHost, nakadiPort);
        System.setProperty("NAKADI_URL", nakadiUrl);
    }

    protected static void initializeNakadiEventTypes() throws IOException {
        final String[] eventTypes = new ClassPathResource("/event-types").getFile().list();
        for (String type : eventTypes) {
            createEventType(type);
        }
    }

    protected static void createEventType(String filename) throws IOException {
        final String eventName = filename.substring(0, filename.length() - ".json".length());
        byte[] data = copyToByteArray(new ClassPathResource("/event-types/" + filename).getInputStream());
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        http.postForLocation(nakadiUrl + "/event-types", new HttpEntity<byte[]>(data, headers));
        JsonNode result = http.getForObject(nakadiUrl + "/event-types/" + eventName, JsonNode.class);
        log.info("Successfully created event type : {}", result);
    }

    @AfterAll
    public static void tearDown() {
        nakadiEnv.close();
    }

    @Test
    public void contextLoads() throws InterruptedException, IOException, ExecutionException {
        assertThat(nakadiUrl).isNotBlank();
        System.out.println(nakadiUrl);
        assertThat(outfitUpdateConsumer).isNotNull();
        assertThat(outfitUpdatePublisher).isNotNull();
        log.info("PUBLISH AN EVENT ...");
        outfitUpdatePublisher.publish("outfit.outfit-update", Collections.singletonList(OutfitUpdateEvent.buildEvent(DataOperation.CREATE, OutfitId.builder().outfitId(12L).build(), UUID.randomUUID().toString())));
        log.info("SLEEP 20 ...");
        TimeUnit.SECONDS.sleep(20);
        log.info("FETCH EVENTS ...");

        outfitUpdatePublisher.publish("outfit.outfit-update", Collections.singletonList(OutfitUpdateEvent.buildEvent(DataOperation.CREATE, OutfitId.builder().outfitId(15L).build(), UUID.randomUUID().toString())));
        outfitUpdatePublisher.publish("outfit.outfit-update", Collections.singletonList(OutfitUpdateEvent.buildEvent(DataOperation.CREATE, OutfitId.builder().outfitId(16L).build(), UUID.randomUUID().toString())));
        outfitUpdatePublisher.publish("outfit.outfit-update", Collections.singletonList(OutfitUpdateEvent.buildEvent(DataOperation.CREATE, OutfitId.builder().outfitId(17L).build(), UUID.randomUUID().toString())));


        log.info("WAIT MAX OF 60 SECONDS ...");
        TimeUnit.SECONDS.sleep(60);
        log.info("DONE");
    }

    static class NakadiEnvironmentContainer extends DockerComposeContainer<NakadiEnvironmentContainer> {
        public NakadiEnvironmentContainer() throws IOException {
            super(new ClassPathResource("/docker-compose-nakadi.yaml").getFile());
        }
    }

    static class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Map<String, Object> props = new HashMap<>();
            props.put("fahrschein.clients.first.nakadi-uri", nakadiUrl);
            applicationContext.getEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("integrationTest", props));
        }
    }
}

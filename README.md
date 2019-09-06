# Fahrschein Spring-Boot-Starter

[![Build Status](https://img.shields.io/travis/jbellmann/fahrschein-spring-boot-starter/master.svg)](https://travis-ci.org/jbellmann/fahrschein-spring-boot-starter)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando.spring/fahrschein-spring-boot-starter.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando.spring/fahrschein-spring-boot-starter)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/riptide/master/LICENSE)

*Fahrschein Spring Boot Starter* is a library that integrates the Fahrschein Nakadi 
client library into a Spring Boot environment. It sets up Nakadi clients and make them 
configurable without writing boilerplate code.

- **Technology stack**: Spring Boot (1.5.x, 2.1.x)
- **Status**:  Alpha

## Example

```yaml
fahrschein:
  defaults:
    nakadi-url: https://example.nakadi-instance.do
    application-name: example-application
    consumer-group: example-consumergroup
    stream-parameters:
      batch-limit: 1000
      max-uncommitted-events: 5000
      batch-flush-timeout: 5
    authorizations:
      anyReader: true
      admins:
        service: example-application
    oauth:
      enabled: true
      access-token-id: nakadi
    record-metrics: true
    backoff:
      enabled: true
  consumers:
    article-01:
      topics:
      - 'article-example_01924c48-AAAA-40c2-DDDD-ab582e6db6f4_ms'
      stream-parameters:
        batch-limit: 5
        max-uncommitted-events: 20
      backoff:
        enabled: false
    article-02:
      topics:
      - 'article-example_00f2a393-AAAA-4fc0-DDDD-86e454e6dfa3_ag'
    article-03:
      topics:
      - 'article-example_091dcbdd-AAAA-4f39-DDDD-324eb4599df0_io'
      - 'article-example_7ce94f55-AAAA-4416-DDDD-bf34193a47e8_co'
      - 'article-example_ebf57ebf-AAAA-4ebd-DDDD-6ad519073d2a_us'
      stream-parameters:
        batch-limit: 30
        max-uncommitted-events: 500
```

## Installation

Add the following dependency to your project:

```
    <dependency>
        <groupId>org.zalando.spring</groupId>
        <artifactId>fahrschein-spring-boot-starter</artifactId>
        <version>${version}</version>
    </dependency>
```


Clients are identified by a *Client ID*, for instance `example` in the sample above. You can have as many clients as you want.

### Reference

For a complete overview of available properties, they type and default value please refer to the following table:

|Configuration                                |Data type            |Default / Comment                          |
|:------------------------------------------- |:--------------------|:------------------------------------------|
|`fahrschein`                                 |                     |                                           |
|`├── defaults`                               |                     |                                           |
|`│   ├── nakadi-url`                         |`String`             |none                                       |
|`│   ├── application-name`                   |`String`             |none                                       |
|`│   ├── consumer-group`                     |`String`             |none                                       |
|`│   ├── autostart-enabled`                  |`boolean`            |`true`                                     |
|`│   ├── record-metrics`                     |`boolean`            |`false`                                    |
|`│   ├── read-From`                          |`Position`           |`end` (`begin`)                            |
|`│   ├── oauth`                              |                     |                                           |
|`│   │   ├── enabled`                        |`boolean`            |`false`                                    |
|`│   │   └── access-token-id`                |`String`             |none                                       |
|`│   ├── http`                               |                     |                                           |
|`│   │   ├── socket-timeout`                 |`TimeSpan`           |`5 seconds`                                |
|`│   │   ├── connect-timeout`                |`TimeSpan`           |`5 seconds`                                |
|`│   │   ├── connection-request-timeout`     |`TimeSpan`           |`5 seconds`                                |
|`│   │   ├── content-compression-enabled`    |`boolean`            |`false`                                    |
|`│   │   ├── buffer-size`                    |`int`                |`512`                                      |
|`│   │   ├── connection-time-to-live`        |`TimeSpan`           |`30 seconds`                               |
|`│   │   ├── max-connections-total`          |`int`                |`3`                                        |
|`│   │   ├── max-connections-per-route`      |`int`                |`3`                                        |
|`│   │   ├── evict-expired-connections`      |`boolean`            |`true`                                     |
|`│   │   ├── evict-idle-connections`         |`boolean`            |`true`                                     |
|`│   │   └── max-idle-time`                  |`int`                |`10_000`                                   |
|`│   ├── backoff`                            |                     |                                           |
|`│   │   ├── enabled`                        |`boolean`            |`false`                                    |
|`│   │   ├── intial-delay`                   |`TimeSpan`           |`500 milliseconds`                         |
|`│   │   ├── max-delay`                      |`TimeSpan`           |`10 minutes`                               |
|`│   │   ├── backoff-factor`                 |`double`             |`1.5`                                      |
|`│   │   ├── max-retries`                    |`int`                |`1`                                        |
|`│   │   └── jitter`                         |                     |                                           |
|`│   │       ├── enabled`                    |`boolean`            |`false`                                    |
|`│   │       └── type`                       |`JitterType`         |`equal` (`full`)                           |
|`│   ├── threads`                            |                     |                                           |
|`│   │   └── listener-pool-size`             |`int`                |`1`                                        |
|`│   ├── oauth`                              |                     |                                           |
|`│   │   ├── enabled`                        |`boolean`            |`false`                                    |
|`│   │   └── access-token-id`                |`String`             |none                                       |
|`│   └── stream-parameters`                  |                                                                 |
|`│       ├── batch-limit`                    |`int`                |none                                       |
|`│       ├── stream-limit`                   |`int`                |none                                       |
|`│       ├── batch-flush-timeout`            |`int`                |none                                       |
|`│       ├── stream-timeout`                 |`int`                |none                                       |
|`│       └── max-uncommitted-events`         |`int`                |none                                       |
|`│`                                          |                     |                                           |
|`└── consumers`                              |                     |                                           |
|`    └── <id>`                               |`String`             |                                           |
|`        ├── topics`                         |`List<String>`       |                                           |
|`        ├── nakadi-url`                     |`String`             |none                                       |
|`        ├── application-name`               |`String`             |none                                       |
|`        ├── consumer-group`                 |`String`             |none                                       |
|`        ├── autostart-enabled`              |`boolean`            |`true`                                     |
|`        ├── record-metrics`                 |`boolean`            |`false`                                    |
|`        ├── read-From`                      |`Position`           |`end` (`begin`)                            |
|`        ├── oauth`                          |                     |                                           |
|`        │   ├── enabled`                    |`boolean`            |`false`                                    |
|`        │   └── access-token-id`            |`String`             |none                                       |
|`        ├── http`                           |                     |                                           |
|`        │   ├── socket-timeout`             |`TimeSpan`           |`5 seconds`                                |
|`        │   ├── connect-timeout`            |`TimeSpan`           |`5 seconds`                                |
|`        │   ├── connection-request-timeout` |`TimeSpan`           |`5 seconds`                                |
|`        │   ├── content-compression-enabled`|`boolean`            |`false`                                    |
|`        │   ├── buffer-size`                |`int`                |`512`                                      |
|`        │   ├── connection-time-to-live`    |`TimeSpan`           |`30 seconds`                               |
|`        │   ├── max-connections-total`      |`int`                |`3`                                        |
|`        │   ├── max-connections-per-route`  |`int`                |`3`                                        |
|`        │   ├── evict-expired-connections`  |`boolean`            |`true`                                     |
|`        │   ├── evict-idle-connections`     |`boolean`            |`true`                                     |
|`        │   └── max-idle-time`              |`int`                |`10_000`                                   |
|`        ├── backoff`                        |                     |                                           |
|`        │   ├── enabled`                    |`boolean`            |`false`                                    |
|`        │   ├── intial-delay`               |`TimeSpan`           |`500 milliseconds`                         |
|`        │   ├── max-delay`                  |`TimeSpan`           |`10 minutes`                               |
|`        │   ├── backoff-factor`             |`double`             |`1.5`                                      |
|`        │   ├── max-retries`                |`int`                |`1`                                        |
|`        │   └── jitter`                     |                     |                                           |
|`        │       ├── enabled`                |`boolean`            |`false`                                    |
|`        │       └── type`                   |`JitterType`         |`equal` (`full`)                           |
|`        ├── threads`                        |                     |                                           |
|`        │   └── listener-pool-size`         |`int`                |`1`                                        |
|`        ├── oauth`                          |                     |                                           |
|`        │   ├── enabled`                    |`boolean`            |`false`                                    |
|`        │   └── access-token-id`            |`String`             |none                                       |
|`        └── stream-parameters`              |                                                                 |
|`            ├── batch-limit`                |`int`                |none                                       |
|`            ├── stream-limit`               |`int`                |none                                       |
|`            ├── batch-flush-timeout`        |`int`                |none                                       |
|`            ├── stream-timeout`             |`int`                |none                                       |
|`            └── max-uncommitted-events`     |`int`                |none                                       |


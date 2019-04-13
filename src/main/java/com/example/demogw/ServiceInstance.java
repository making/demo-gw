package com.example.demogw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceInstance extends SimpleDiscoveryProperties.SimpleServiceInstance {

    private static final Logger log = LoggerFactory.getLogger(ServiceInstance.class);

    private String healthCheckEndpoint;

    private SimpleDiscoveryProperties.SimpleServiceInstance fallback;

    private RestTemplate restTemplate;

    private AtomicBoolean healthy = new AtomicBoolean(true);

    public String getHealthCheckEndpoint() {
        return healthCheckEndpoint;
    }

    public void setHealthCheckEndpoint(String healthCheckEndpoint) {
        this.healthCheckEndpoint = healthCheckEndpoint;
    }

    public SimpleDiscoveryProperties.SimpleServiceInstance getFallback() {
        return fallback;
    }

    public void setFallback(URI fallbackUrl) {
        SimpleDiscoveryProperties.SimpleServiceInstance instance = new SimpleDiscoveryProperties.SimpleServiceInstance();
        instance.setUri(fallbackUrl);
        this.fallback = instance;
    }

    org.springframework.cloud.client.ServiceInstance healthyInstance() {
        if (this.healthy.get()) {
            return this;
        } else {
            return this.fallback;
        }
    }

    void init() {
        this.restTemplate = new RestTemplateBuilder()
            .rootUri(this.getUri().toString())
            .setReadTimeout(Duration.ofMillis(500))
            .setConnectTimeout(Duration.ofMillis(500))
            .build();
    }

    void healthCheck() {
        try {
            this.restTemplate.getForObject(this.healthCheckEndpoint, String.class);
            this.healthy.set(true);
        } catch (RestClientException e) {
            log.warn("[{}] Health check failed. Fallback to {}: {}", this.getUri(), this.fallback.getUri(), e.getMessage());
            this.healthy.set(false);
        }
    }

    @Override
    public String toString() {
        return "ServiceInstance{uri=" + this.getUri() + ", " +
            "healthCheckEndpoint=" + healthCheckEndpoint +
            ", fallback=" + fallback.getUri() + ", healthy=" + healthy + "}";
    }
}

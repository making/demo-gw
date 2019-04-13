package com.example.demogw;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "service")
public class ServiceInstances {

    private Map<String, ServiceInstance> instances;

    public Map<String, ServiceInstance> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, ServiceInstance> instances) {
        this.instances = instances;
    }

    @PostConstruct
    void init() {
        this.instances.values().forEach(ServiceInstance::init);
    }

    @Scheduled(fixedDelayString = "PT10S")
    public void healthCheck() {
        this.instances.values().forEach(ServiceInstance::healthCheck);
    }

}

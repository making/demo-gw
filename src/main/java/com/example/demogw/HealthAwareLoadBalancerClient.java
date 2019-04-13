package com.example.demogw;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class HealthAwareLoadBalancerClient implements LoadBalancerClient {

    private final ServiceInstances serviceInstances;

    public HealthAwareLoadBalancerClient(ServiceInstances serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    @Override
    public ServiceInstance choose(String serviceId) {
        com.example.demogw.ServiceInstance serviceInstance = this.serviceInstances.getInstances().get(serviceId);
        return serviceInstance.healthyInstance();
    }

    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request) {
        throw new UnsupportedOperationException("execute is not implemented (not used by Spring Cloud Gateway)");
    }

    @Override
    public <T> T execute(String serviceId, ServiceInstance serviceInstance,
                         LoadBalancerRequest<T> request) {
        throw new UnsupportedOperationException("execute is not implemented (not used by Spring Cloud Gateway)");
    }

    @Override
    public URI reconstructURI(ServiceInstance instance, URI original) {
        URI uri = UriComponentsBuilder.fromUri(original)
            .scheme(instance.getScheme())
            .host(instance.getHost())
            .port(instance.getPort())
            .build().toUri();
        return uri;
    }
}

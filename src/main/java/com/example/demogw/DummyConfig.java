package com.example.demogw;

import com.sun.net.httpserver.HttpHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.support.SimpleHttpServerFactoryBean;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Configuration
public class DummyConfig {

    @Bean
    public SimpleHttpServerFactoryBean dc1() {
        SimpleHttpServerFactoryBean factoryBean = new SimpleHttpServerFactoryBean();
        factoryBean.setPort(19999);
        AtomicInteger counter = new AtomicInteger(0);
        factoryBean.setContexts(new HashMap<String, HttpHandler>() {

            {
                put("/get", exchange -> {
                    byte[] body = "Hello from DC1!".getBytes();
                    exchange.getResponseHeaders().set(CONTENT_TYPE, TEXT_PLAIN_VALUE);
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                    exchange.close();
                });
                put("/health", exchange -> {
                    boolean health = counter.getAndIncrement() % 5 < 3; // ok ok ok ng ng ok ok ok ng ng ...
                    byte[] body = (health ? "OK" : "NG").getBytes();
                    exchange.getResponseHeaders().set(CONTENT_TYPE, TEXT_PLAIN_VALUE);
                    exchange.sendResponseHeaders((health ? 200 : 503), body.length);
                    exchange.getResponseBody().write(body);
                    exchange.close();
                });
            }
        });
        return factoryBean;
    }

    @Bean
    public SimpleHttpServerFactoryBean dc2() {
        SimpleHttpServerFactoryBean factoryBean = new SimpleHttpServerFactoryBean();
        factoryBean.setPort(29999);
        factoryBean.setContexts(new HashMap<String, HttpHandler>() {

            {
                put("/get", exchange -> {
                    byte[] body = "Hello from DC2!".getBytes();
                    exchange.getResponseHeaders().set(CONTENT_TYPE, TEXT_PLAIN_VALUE);
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                    exchange.close();
                });
                put("/health", exchange -> {
                    byte[] body = "OK".getBytes();
                    exchange.getResponseHeaders().set(CONTENT_TYPE, TEXT_PLAIN_VALUE);
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                    exchange.close();
                });
            }
        });
        return factoryBean;
    }
}

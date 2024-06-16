package com.youyi.seataconsumer;

import io.seata.core.context.RootContext;
import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

// 排除 SeataAutoConfiguration
@SpringBootApplication(exclude = SeataAutoConfiguration.class)
public class SeataConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataConsumerApplication.class, args);
    }

}

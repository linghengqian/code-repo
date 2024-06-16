package com.youyi.seataprovider;

import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = SeataAutoConfiguration.class)
public class SeataProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataProviderApplication.class, args);
    }

}

package com.youyi.seataconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;

@TestConfiguration
public class TestjarApplication {
    public static void main(String[] args) {
        SpringApplication.from(SeataConsumerApplication::main)
                .with(TestjarApplication.class)
                .run(args);
    }

    @Bean
    static CommonsExecWebServerFactoryBean seataProviderServer() {
        return CommonsExecWebServerFactoryBean.builder()
                // For https://github.com/spring-projects-experimental/spring-boot-testjars/issues/32 .
                .mainClass("org.springframework.boot.loader.launch.JarLauncher")
                .classpath((cp) -> cp
                                .files("../seata-provider/build/libs/seata-provider-shardingsphere-sb-0.0.1-SNAPSHOT.jar")
                );
    }
}

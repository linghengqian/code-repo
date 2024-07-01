package com.youyi.seataconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;
import org.springframework.experimental.boot.test.context.DynamicProperty;
import org.springframework.experimental.boot.test.context.EnableDynamicProperty;

@TestConfiguration
@EnableDynamicProperty
public class SeataConsumerApplicationTests {

    public static void main(String[] args) {
        SpringApplication
                .from(SeataConsumerApplication::main)
                .with(SeataConsumerApplicationTests.class)
                .run(args);
    }

    @Bean
    @DynamicProperty(name = "provider.port", value = "port")
    static CommonsExecWebServerFactoryBean providerBean() {
        return CommonsExecWebServerFactoryBean
                .builder()
                .mainClass("org.springframework.boot.loader.launch.JarLauncher")
                .classpath(cp -> cp.files("../seata-provider/build/libs/seata-provider-shardingsphere-sb-0.0.1-SNAPSHOT.jar"));
    }

}

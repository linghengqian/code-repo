package com.youyi.seataconsumer;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

@SpringBootTest(classes = SeataConsumerApplicationTests.class)
public abstract class AbstractTests {
    static MySQLContainer<?> mySQLContainer1 = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withPassword("123456")
            .withDatabaseName("seata")
            .withCreateContainerCmdModifier(e -> e.withHostConfig(new HostConfig().withPortBindings(new Ports(new ExposedPort(3306), Ports.Binding.bindPort(3306)))))
            .withInitScript("init.sql");
    static MySQLContainer<?> mySQLContainer2 = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withPassword("123456")
            .withDatabaseName("seata")
            .withCreateContainerCmdModifier(e -> e.withHostConfig(new HostConfig().withPortBindings(new Ports(new ExposedPort(3306), Ports.Binding.bindPort(3307)))))
            .withInitScript("init.sql");
    static GenericContainer<?> seataContainer = new GenericContainer<>("seataio/seata-server:2.0.0")
            .withCreateContainerCmdModifier(e -> e.withHostConfig(new HostConfig().withPortBindings(new Ports(new ExposedPort(8091), Ports.Binding.bindPort(18091)))));

    @BeforeAll
    static void init() {
        mySQLContainer1.start();
        mySQLContainer2.start();
        seataContainer.start();
    }

    @Autowired
    ConsumerController consumerController;

    @Bean
    DataSource dataSource3306() {
        return DataSourceBuilder
                .create()
                .url(" jdbc:mysql://localhost:3306/seata")
                .username("root")
                .password("123456")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    @Bean
    DataSource dataSource3307() {
        return DataSourceBuilder
                .create()
                .url(" jdbc:mysql://localhost:3307/seata")
                .username("root")
                .password("123456")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}

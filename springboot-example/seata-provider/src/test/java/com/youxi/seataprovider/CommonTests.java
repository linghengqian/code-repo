package com.youxi.seataprovider;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.youyi.seataprovider.SeataProviderApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"resource", "deprecation", "SameParameterValue", "SqlDialectInspection", "SqlNoDataSourceInspection"})
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SeataProviderApplication.class)
class CommonTests {

    @Container
    public static final MySQLContainer<?> mySQLContainer2 = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withPassword("123456")
            .withDatabaseName("seata")
            .withCreateContainerCmdModifier(e -> e.withHostConfig(new HostConfig().withPortBindings(new Ports(new ExposedPort(3306), Ports.Binding.bindPort(3307)))))
            .withInitScript("init.sql");

    @Container
    public static final FixedHostPortGenericContainer<?> seataContainer = new FixedHostPortGenericContainer<>("seataio/seata-server:2.0.0")
            .withFixedExposedPort(18091, 8091)
            .withExposedPorts(7091)
            .waitingFor(Wait.forHttp("/health").forPort(7091).forStatusCode(401))
            .dependsOn(mySQLContainer2);

    @Test
    void test(@LocalServerPort Integer port) throws SQLException {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        final long id = 1L;
        // 1st assertThrows
        assertThrows(HttpServerErrorException.class, () -> restTemplate.getForObject("http://localhost:" + port + "/provide_with_error/" + id, String.class));
        try (Connection connection = openConnection(3307);
             PreparedStatement ps = connection.prepareStatement("SELECT id, money FROM account where id = 1");
             ResultSet resultSet = ps.executeQuery()
        ) {
            resultSet.next();
            assertEquals(100.0, resultSet.getDouble("money"));
        }
        // 2nd assertDoesNotThrow
        assertDoesNotThrow(() -> {
            String result = restTemplate.getForObject("http://localhost:" + port + "/provide/" + id, String.class);
            assertEquals("OK", result);
        });
        try (Connection connection = openConnection(3307);
             PreparedStatement ps = connection.prepareStatement("SELECT id, money FROM account where id = 1");
             ResultSet resultSet = ps.executeQuery()
        ) {
            resultSet.next();
            assertEquals(200.0 + 100.0, resultSet.getDouble("money"));
        }
        /*
         * Make sure that the Seata Server container is shut down before the database container to avoid invalid Error Log.
         *  Of course there is an elegant way, but it is not easy to understand if `org.apache.shardingsphere:shardingsphere-infra-database-testcontainers` is introduced.
         * */
        seataContainer.close();
    }

    private Connection openConnection(final int mySQLHostPort) throws SQLException {
        Properties props = new Properties();
        props.put("user", "root");
        props.put("password", "123456");
        return DriverManager.getConnection("jdbc:mysql://localhost:" + mySQLHostPort + "/seata", props);
    }
}
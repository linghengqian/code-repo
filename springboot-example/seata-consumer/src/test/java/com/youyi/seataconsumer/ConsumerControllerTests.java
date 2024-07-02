package com.youyi.seataconsumer;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection", "resource", "deprecation"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TestjarApplication.class)
@Testcontainers
public class ConsumerControllerTests {

    @Autowired
    private CommonsExecWebServer commonsExecWebServer;

    @Container
    public static final MySQLContainer<?> mySQLContainer1 = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withPassword("123456")
            .withDatabaseName("seata")
            .withCreateContainerCmdModifier(e -> e.withHostConfig(new HostConfig().withPortBindings(new Ports(new ExposedPort(3306), Ports.Binding.bindPort(3306)))))
            .withInitScript("init.sql");

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
            .dependsOn(mySQLContainer1, mySQLContainer2);

    /**
     * initial state of the data:
     * datasource/3306 in consumer: id(1), money(100.0)
     * datasource/3307 in provider: id(1), money(100.0)
     * <p>
     * test scenario:
     * 1.seata-consumer update the money by adding 200 for the id(1) in datasource/3306
     * 2.seata-consumer calls seata-provider
     * 2.1 seata-provider update the money by adding 200 for the id(1) in datasource/3307
     * 3.seata-consumer throw ArithmeticException
     * <p>
     * expect result:
     * the data should be kept the same as the initial state.
     * <p>
     * actual result:
     * datasource3306: id(1), money(100.0)
     * datasource3307: id(1), money(300.0)
     */
    @Test
    void testConsumerController(@LocalServerPort Integer port) throws SQLException {
        assertThrows(HttpServerErrorException.class, () -> {
            RestTemplate restTemplate = new RestTemplateBuilder().build();
            restTemplate.getForObject("http://localhost:" + port + "/consume/" + 1L + "/" + commonsExecWebServer.getPort(), String.class);
        });
        // 3306 in seata-consumer-shardingsphere-sb
        assertEquals(100.0, getMoney(3306));
        // the below assertion will fail
        // 3307 in seata-provider-shardingsphere-sb
        assertEquals(100.0, getMoney(3307));
        /*
         * Make sure that the Seata Server container is shut down before the database container to avoid invalid Error Log.
         *  Of course there is an elegant way, but it is not easy to understand if `org.apache.shardingsphere:shardingsphere-infra-database-testcontainers` is introduced.
         * */
        seataContainer.close();
    }

    private double getMoney(final int hostPort) throws SQLException {
        try (
                Connection connection = this.openConnection(hostPort);
                PreparedStatement ps = connection.prepareStatement("SELECT id, money FROM account where id = 1");
                ResultSet resultSet = ps.executeQuery()) {
            resultSet.next();
            return resultSet.getDouble("money");
        }
    }

    private Connection openConnection(final int hostPort) throws SQLException {
        Properties props = new Properties();
        props.put("user", "root");
        props.put("password", "123456");
        return DriverManager.getConnection("jdbc:mysql://localhost:" + hostPort + "/seata", props);
    }
}

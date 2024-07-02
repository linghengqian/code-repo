# code-repo

- For https://github.com/apache/shardingsphere/issues/31715 .
- Because of the state management involved in multiple microservices,
  please ensure that ports `3306`, `3307`, `8081` and `18091` on the host device are free for use by
  `testcontainers-java`.

## 1st

- 1st, Verified `seata-provider` under `Ubuntu 22.04.4 LTS` with `Docker Engine` and `SDKMAN!`.

```bash
sdk install java 17.0.11-ms
sdk use java 17.0.11-ms

git clone git@github.com:linghengqian/code-repo.git -b test
cd ./code-repo/
cd ./springboot-example/seata-provider/
./gradlew clean test
```

## 2nd

- 2nd, Verified `seata-consumer` with `seata-provider` under `Ubuntu 22.04.4 LTS` with `Docker Engine` and `SDKMAN!`.

```bash
sdk install java 17.0.11-ms
sdk use java 17.0.11-ms

git clone git@github.com:linghengqian/code-repo.git -b test
cd ./code-repo/
cd ./springboot-example/seata-provider/
./gradlew clean bootJar -x test

cd ../seata-consumer/
./gradlew clean test
```

- Log as follows.

```bash
$ ./gradlew clean test
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

> Task :test

ConsumerControllerTests > testConsumerController(Integer) FAILED
    org.opentest4j.AssertionFailedError at ConsumerControllerTests.java:86

2024-07-07T00:05:43.044+08:00  INFO 72424 --- [seata-consumer] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2024-07-07T00:05:43.045+08:00  INFO 72424 --- [seata-consumer] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

> Task :test FAILED

1 test completed, 1 failed

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///home/linghengqian/TwinklingLiftWorks/git/public/code-repo/springboot-example/seata-consumer/build/reports/tests/test/index.html

* Try:
> Run with --scan to get full insights.

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/8.8/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.

BUILD FAILED in 36s
6 actionable tasks: 6 executed
```
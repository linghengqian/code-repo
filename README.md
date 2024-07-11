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

git clone git@github.com:linghengqian/shardingsphere.git -b seata-fix
cd ./shardingsphere/
./mvnw clean install -Prelease -T1C -DskipTests -Djacoco.skip=true -Dcheckstyle.skip=true -Drat.skip=true -Dmaven.javadoc.skip=true
cd ../

git clone git@github.com:linghengqian/code-repo.git -b re-test
cd ./code-repo/
cd ./springboot-example/seata-provider/
./gradlew clean test
```

## 2nd

- 2nd, Verified `seata-consumer` with `seata-provider` under `Ubuntu 22.04.4 LTS` with `Docker Engine` and `SDKMAN!`.

```bash
sdk install java 17.0.11-ms
sdk use java 17.0.11-ms

git clone git@github.com:linghengqian/shardingsphere.git -b seata-fix
cd ./shardingsphere/
./mvnw clean install -Prelease -T1C -DskipTests -Djacoco.skip=true -Dcheckstyle.skip=true -Drat.skip=true -Dmaven.javadoc.skip=true
cd ../

git clone git@github.com:linghengqian/code-repo.git -b re-test
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
2024-07-13T16:44:29.561+08:00  INFO 73424 --- [seata-consumer] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2024-07-13T16:44:29.564+08:00  INFO 73424 --- [seata-consumer] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/8.8/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.

BUILD SUCCESSFUL in 43s
6 actionable tasks: 6 executed
```
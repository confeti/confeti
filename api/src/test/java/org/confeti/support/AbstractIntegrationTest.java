package org.confeti.support;

import com.datastax.driver.core.Cluster;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    public static final String CASSANDRA_DOCKER_IMAGE_NAME = "cassandra:3.11.8";
    public static final String CREATE_KEYSPACE_QUERY = """
                                                       CREATE KEYSPACE IF NOT EXISTS test WITH replication =
                                                       {'class':'SimpleStrategy','replication_factor':'1'};
                                                       """;

    static CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse(CASSANDRA_DOCKER_IMAGE_NAME));

    @DynamicPropertySource
    static void cassandraProperties(final DynamicPropertyRegistry registry) {
        cassandra.start();
        final var host = cassandra.getHost();
        final var mappedPort = cassandra.getMappedPort(CassandraContainer.CQL_PORT);
        try (final var cluster = Cluster.builder()
                .addContactPoint(host)
                .withPort(mappedPort)
                .withoutJMXReporting()
                .build()) {
            final var session = cluster.newSession();
            session.execute(CREATE_KEYSPACE_QUERY);
        }
        registry.add(
                "confeti.cassandra.session.contact-points",
                () -> String.format("%s:%d", host, mappedPort));
    }
}

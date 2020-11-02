package org.confeti.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

@Configuration
@Slf4j
public class CassandraConfig {
    private static final String DRIVER_LOCAL_CONFIG = "application-local.conf";
    private static final String DRIVER_ASTRA_CONFIG = "application-astra.conf";

    @Value("${confeti.cassandra.local:true}")
    private boolean isCassandraLocal;

    @Value("${confeti.cassandra.session.durable:true}")
    private boolean isDurableWrites;

    @Value("${confeti.cassandra.session.name}")
    private String cassandraSessionName;

    @Value("${confeti.cassandra.session.keyspace}")
    private String cassandraSessionKeyspace;

    @Value("${confeti.cassandra.session.rf}")
    private Integer cassandraRF;

    @Bean
    public CqlSession cqlSession() {
        CqlSession cqlSession;
        if (isCassandraLocal) {
            final var configReader = DriverConfigLoader.fromClasspath(DRIVER_LOCAL_CONFIG);
            cqlSession = CqlSession.builder().withConfigLoader(configReader).build();
            cqlSession.execute(createKeyspace(cassandraSessionKeyspace).ifNotExists()
                    .withSimpleStrategy(cassandraRF)
                    .withDurableWrites(isDurableWrites)
                    .build());
            cqlSession.execute(String.format("use %s", cassandraSessionName));
        } else {
            log.info("Loading configuration to Astra");
            final var configReader = DriverConfigLoader.fromClasspath(DRIVER_ASTRA_CONFIG);
            cqlSession = CqlSession.builder().withConfigLoader(configReader).build();
        }

        return cqlSession;
    }
}

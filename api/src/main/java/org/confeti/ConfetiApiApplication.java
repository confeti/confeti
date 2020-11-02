package org.confeti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;

@SpringBootApplication(exclude = CassandraAutoConfiguration.class)
public class ConfetiApiApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ConfetiApiApplication.class, args);
    }
}

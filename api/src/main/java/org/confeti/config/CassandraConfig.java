package org.confeti.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.confeti.db.dao.conference.ConferenceBySpeakerDao;
import org.confeti.db.dao.conference.ConferenceDao;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.dao.report.stats.ReportStatsByConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerDao;
import org.confeti.db.dao.speaker.SpeakerByConferenceDao;
import org.confeti.db.dao.speaker.SpeakerDao;
import org.confeti.db.mapper.conference.ConferenceBySpeakerDaoMapperBuilder;
import org.confeti.db.mapper.conference.ConferenceDaoMapperBuilder;
import org.confeti.db.mapper.report.ReportByConferenceDaoMapperBuilder;
import org.confeti.db.mapper.report.ReportBySpeakerDaoMapperBuilder;
import org.confeti.db.mapper.report.ReportByTagDaoMapperBuilder;
import org.confeti.db.mapper.report.ReportDaoMapperBuilder;
import org.confeti.db.mapper.report.stats.ReportStatsByCompanyDaoMapperBuilder;
import org.confeti.db.mapper.report.stats.ReportStatsByConferenceDaoMapperBuilder;
import org.confeti.db.mapper.report.stats.ReportStatsBySpeakerDaoMapperBuilder;
import org.confeti.db.mapper.speaker.SpeakerByConferenceDaoMapperBuilder;
import org.confeti.db.mapper.speaker.SpeakerDaoMapperBuilder;
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
            cqlSession.execute(String.format("use %s", cassandraSessionKeyspace));
        } else {
            log.info("Loading configuration to Astra");
            final var configReader = DriverConfigLoader.fromClasspath(DRIVER_ASTRA_CONFIG);
            cqlSession = CqlSession.builder().withConfigLoader(configReader).build();
        }

        return cqlSession;
    }

    @Bean
    public ConferenceDao conferenceDao(final CqlSession cqlSession) {
        final var conferenceMapper = new ConferenceDaoMapperBuilder(cqlSession).build();
        conferenceMapper.createSchema(cqlSession);
        return conferenceMapper.conferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public SpeakerDao speakerDao(final CqlSession cqlSession) {
        final var speakerMapper = new SpeakerDaoMapperBuilder(cqlSession).build();
        speakerMapper.createSchema(cqlSession);
        return speakerMapper.speakerDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportDao reportDao(final CqlSession cqlSession) {
        final var reportMapper = new ReportDaoMapperBuilder(cqlSession).build();
        reportMapper.createSchema(cqlSession);
        return reportMapper.reportDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public SpeakerByConferenceDao speakerByConferenceDao(final CqlSession cqlSession) {
        final var speakerByConferenceMapper = new SpeakerByConferenceDaoMapperBuilder(cqlSession).build();
        speakerByConferenceMapper.createSchema(cqlSession);
        return speakerByConferenceMapper.speakerByConferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ConferenceBySpeakerDao conferenceBySpeakerDao(final CqlSession cqlSession) {
        final var conferenceBySpeakerMapper = new ConferenceBySpeakerDaoMapperBuilder(cqlSession).build();
        conferenceBySpeakerMapper.createSchema(cqlSession);
        return conferenceBySpeakerMapper.conferenceBySpeakerDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportByTagDao reportByTagDao(final CqlSession cqlSession) {
        final var reportByTagMapper = new ReportByTagDaoMapperBuilder(cqlSession).build();
        reportByTagMapper.createSchema(cqlSession);
        return reportByTagMapper.reportByTagDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportByConferenceDao reportByConferenceDao(final CqlSession cqlSession) {
        final var reportByConferenceMapper = new ReportByConferenceDaoMapperBuilder(cqlSession).build();
        reportByConferenceMapper.createSchema(cqlSession);
        return reportByConferenceMapper.reportByConferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportBySpeakerDao reportBySpeakerDao(final CqlSession cqlSession) {
        final var reportBySpeakerMapper = new ReportBySpeakerDaoMapperBuilder(cqlSession).build();
        reportBySpeakerMapper.createSchema(cqlSession);
        return reportBySpeakerMapper.reportBySpeakerDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportStatsByConferenceDao reportStatsByConferenceDaoMapperBuilder(final CqlSession cqlSession) {
        final var reportStatsByConferenceMapper = new ReportStatsByConferenceDaoMapperBuilder(cqlSession).build();
        reportStatsByConferenceMapper.createSchema(cqlSession);
        return reportStatsByConferenceMapper.reportStatsByConferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportStatsBySpeakerDao reportStatsBySpeakerDao(final CqlSession cqlSession) {
        final var reportStatsBySpeakerMapper = new ReportStatsBySpeakerDaoMapperBuilder(cqlSession).build();
        reportStatsBySpeakerMapper.createSchema(cqlSession);
        return reportStatsBySpeakerMapper.reportStatsBySpeakerDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportStatsByCompanyDao reportStatsByCompanyDao(final CqlSession cqlSession) {
        final var reportStatsByCompanyMapper = new ReportStatsByCompanyDaoMapperBuilder(cqlSession).build();
        reportStatsByCompanyMapper.createSchema(cqlSession);
        return reportStatsByCompanyMapper.reportStatsByCompanyDao(cqlSession.getKeyspace().get());
    }
}

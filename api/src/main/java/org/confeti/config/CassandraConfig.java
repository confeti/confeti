package org.confeti.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.internal.core.ContactPoints;
import lombok.extern.slf4j.Slf4j;
import org.confeti.db.dao.company.CompanyDao;
import org.confeti.db.dao.conference.ConferenceBySpeakerDao;
import org.confeti.db.dao.conference.ConferenceDao;
import org.confeti.db.dao.report.ReportByCompanyDao;
import org.confeti.db.dao.report.ReportByConferenceDao;
import org.confeti.db.dao.report.ReportBySpeakerDao;
import org.confeti.db.dao.report.ReportByTagDao;
import org.confeti.db.dao.report.ReportDao;
import org.confeti.db.dao.report.stats.ReportStatsByCompanyDao;
import org.confeti.db.dao.report.stats.ReportStatsByConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForConferenceDao;
import org.confeti.db.dao.report.stats.ReportStatsBySpeakerForYearDao;
import org.confeti.db.dao.speaker.SpeakerByConferenceDao;
import org.confeti.db.dao.speaker.SpeakerDao;
import org.confeti.db.mapper.company.CompanyDaoMapper;
import org.confeti.db.mapper.company.CompanyDaoMapperBuilder;
import org.confeti.db.mapper.conference.ConferenceDaoMapper;
import org.confeti.db.mapper.conference.ConferenceDaoMapperBuilder;
import org.confeti.db.mapper.report.ReportDaoMapper;
import org.confeti.db.mapper.report.ReportDaoMapperBuilder;
import org.confeti.db.mapper.report.ReportStatsDaoMapper;
import org.confeti.db.mapper.report.ReportStatsDaoMapperBuilder;
import org.confeti.db.mapper.speaker.SpeakerDaoMapper;
import org.confeti.db.mapper.speaker.SpeakerDaoMapperBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;

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

    @Value("${confeti.cassandra.session.contact-points:}")
    private List<String> cassandraContactPoints;

    @Bean
    public CqlSession cqlSession() {
        CqlSession cqlSession;
        if (isCassandraLocal) {
            final var configReader = DriverConfigLoader.fromClasspath(DRIVER_LOCAL_CONFIG);
            final var cqlSessionBuilder = CqlSession.builder().withConfigLoader(configReader);
            if (cassandraContactPoints != null && !cassandraContactPoints.isEmpty()) {
                cqlSessionBuilder.addContactEndPoints(ContactPoints.merge(new HashSet<>(), cassandraContactPoints, true));
            }
            cqlSession = cqlSessionBuilder.build();
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
    public ConferenceDaoMapper conferenceDaoMapper(final CqlSession cqlSession) {
        return new ConferenceDaoMapperBuilder(cqlSession).build();
    }

    @Bean
    public SpeakerDaoMapper speakerDaoMapper(final CqlSession cqlSession) {
        return new SpeakerDaoMapperBuilder(cqlSession).build();
    }

    @Bean
    public CompanyDaoMapper companyDaoMapper(final CqlSession cqlSession) {
        return new CompanyDaoMapperBuilder(cqlSession).build();
    }

    @Bean
    public ReportDaoMapper reportDaoMapper(final CqlSession cqlSession) {
        return new ReportDaoMapperBuilder(cqlSession).build();
    }

    @Bean
    public ReportStatsDaoMapper reportStatsDaoMapper(final CqlSession cqlSession) {
        return new ReportStatsDaoMapperBuilder(cqlSession).build();
    }

    @Bean
    public ConferenceDao conferenceDao(final CqlSession cqlSession,
                                       final ConferenceDaoMapper conferenceDaoMapper) {
        conferenceDaoMapper.createConferenceTable(cqlSession);
        return conferenceDaoMapper.conferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ConferenceBySpeakerDao conferenceBySpeakerDao(final CqlSession cqlSession,
                                                         final ConferenceDaoMapper conferenceDaoMapper) {
        conferenceDaoMapper.createConferenceBySpeakerTable(cqlSession);
        return conferenceDaoMapper.conferenceBySpeakerDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public SpeakerDao speakerDao(final CqlSession cqlSession,
                                 final SpeakerDaoMapper speakerDaoMapper) {
        speakerDaoMapper.createSpeakerTable(cqlSession);
        return speakerDaoMapper.speakerDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public SpeakerByConferenceDao speakerByConferenceDao(final CqlSession cqlSession,
                                                         final SpeakerDaoMapper speakerDaoMapper) {
        speakerDaoMapper.createSpeakerByConferenceTable(cqlSession);
        return speakerDaoMapper.speakerByConferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public CompanyDao companyDao(final CqlSession cqlSession,
                                 final CompanyDaoMapper companyDaoMapper) {
        companyDaoMapper.createCompanyTable(cqlSession);
        return companyDaoMapper.companyDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportDao reportDao(final CqlSession cqlSession,
                               final ReportDaoMapper reportDaoMapper) {
        reportDaoMapper.createReportTable(cqlSession);
        return reportDaoMapper.reportDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportByConferenceDao reportByConferenceDao(final CqlSession cqlSession,
                                                       final ReportDaoMapper reportDaoMapper) {
        reportDaoMapper.createReportByConferenceTable(cqlSession);
        return reportDaoMapper.reportByConferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportBySpeakerDao reportBySpeakerDao(final CqlSession cqlSession,
                                                 final ReportDaoMapper reportDaoMapper) {
        reportDaoMapper.createReportBySpeakerTable(cqlSession);
        return reportDaoMapper.reportBySpeakerDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportByTagDao reportByTagDao(final CqlSession cqlSession,
                                         final ReportDaoMapper reportDaoMapper) {
        reportDaoMapper.createReportByTagTable(cqlSession);
        return reportDaoMapper.reportByTagDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportByCompanyDao reportByCompanyDao(final CqlSession cqlSession,
                                                 final ReportDaoMapper reportDaoMapper) {
        reportDaoMapper.createReportByCompanyTable(cqlSession);
        return reportDaoMapper.reportByCompanyDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportStatsByConferenceDao reportStatsByConferenceDaoMapperBuilder(final CqlSession cqlSession,
                                                                              final ReportStatsDaoMapper reportStatsDaoMapper) {
        reportStatsDaoMapper.createReportStatsByConferenceTable(cqlSession);
        return reportStatsDaoMapper.reportStatsByConferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportStatsBySpeakerForYearDao reportStatsBySpeakerForYearDao(final CqlSession cqlSession,
                                                                         final ReportStatsDaoMapper reportStatsDaoMapper) {
        reportStatsDaoMapper.createReportStatsBySpeakerForYearTable(cqlSession);
        return reportStatsDaoMapper.reportStatsBySpeakerForYearDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportStatsBySpeakerForConferenceDao reportStatsBySpeakerForConferenceDao(final CqlSession cqlSession,
                                                                                     final ReportStatsDaoMapper reportStatsDaoMapper) {
        reportStatsDaoMapper.createReportStatsBySpeakerForConfTable(cqlSession);
        return reportStatsDaoMapper.reportStatsBySpeakerForConferenceDao(cqlSession.getKeyspace().get());
    }

    @Bean
    public ReportStatsByCompanyDao reportStatsByCompanyDao(final CqlSession cqlSession,
                                                           final ReportStatsDaoMapper reportStatsDaoMapper) {
        reportStatsDaoMapper.createReportStatsByCompanyTable(cqlSession);
        return reportStatsDaoMapper.reportStatsByCompanyDao(cqlSession.getKeyspace().get());
    }
}

package org.confeti.db.mapper;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createType;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_LOGO;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_NAME;
import static org.confeti.db.model.conference.ConferenceEntity.CONF_ATT_YEAR;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_BIO;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_CONTACT_INFO;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_ID;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_NAME;
import static org.confeti.db.model.udt.ComplexityUDT.COMPLEXITY_UDT;
import static org.confeti.db.model.udt.ComplexityUDT.COMPLEXITY_UDT_ATT_DESCRIPTION;
import static org.confeti.db.model.udt.ComplexityUDT.COMPLEXITY_UDT_ATT_VALUE;
import static org.confeti.db.model.udt.ConferenceShortInfoUDT.CONF_SHORT_INFO_UDT;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_COMPANIES;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_EMAIL;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_LOCATION;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_TWITTER;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_UDT;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_ARTICLE_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_PRES_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_REPO_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_TALK_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_VIDEO_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_UDT;
import static org.confeti.db.model.udt.SpeakerCompanyUDT.SPEAKER_COMPANY_ATT_ADDED_DATE;
import static org.confeti.db.model.udt.SpeakerCompanyUDT.SPEAKER_COMPANY_ATT_NAME;
import static org.confeti.db.model.udt.SpeakerCompanyUDT.SPEAKER_COMPANY_ATT_YEAR;
import static org.confeti.db.model.udt.SpeakerCompanyUDT.SPEAKER_COMPANY_UDT;
import static org.confeti.db.model.udt.SpeakerFullInfoUDT.SPEAKER_FULL_INFO_UDT;
import static org.confeti.db.model.udt.SpeakerShortInfoUDT.SPEAKER_SHORT_INFO_UDT;

@SuppressWarnings({"PMD.TooManyStaticImports"})
public interface BaseMapper {

    /**
     * Create the <i>speaker_company</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS speaker_company (
     *     added_date timestamp,
     *     name text,
     *     year int
     * );
     * </pre>
     */
    default void createSpeakerCompanyUDT(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createType(SPEAKER_COMPANY_UDT).ifNotExists()
                .withField(SPEAKER_COMPANY_ATT_ADDED_DATE, DataTypes.TIMESTAMP)
                .withField(SPEAKER_COMPANY_ATT_NAME, DataTypes.TEXT)
                .withField(SPEAKER_COMPANY_ATT_YEAR, DataTypes.INT)
                .build());
    }

    /**
     * Create the <i>report_source</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS report_source (
     *     presentation_url text,
     *     repo_url text,
     *     repo_url text,
     *     talk_url text,
     *     article_url text
     * );
     * </pre>
     */
    default void createReportSourceUDT(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createType(REPORT_SOURCE_UDT).ifNotExists()
                .withField(REPORT_SOURCE_ATT_PRES_URL, DataTypes.TEXT)
                .withField(REPORT_SOURCE_ATT_REPO_URL, DataTypes.TEXT)
                .withField(REPORT_SOURCE_ATT_VIDEO_URL, DataTypes.TEXT)
                .withField(REPORT_SOURCE_ATT_TALK_URL, DataTypes.TEXT)
                .withField(REPORT_SOURCE_ATT_ARTICLE_URL, DataTypes.TEXT)
                .build());
    }

    /**
     * Create the <i>conference_short_info</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS conference_short_info (
     *     logo text,
     *     name text,
     *     year int,
     * );
     * </pre>
     */
    default void createConferenceShortInfoUDT(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createType(CONF_SHORT_INFO_UDT).ifNotExists()
                .withField(CONF_ATT_LOGO, DataTypes.TEXT)
                .withField(CONF_ATT_NAME, DataTypes.TEXT)
                .withField(CONF_ATT_YEAR, DataTypes.INT)
                .build());
    }

    /**
     * Create the <i>contact_info</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS contact_info (
     *     companies set&lt;frozen&lt;speaker_company&gt;&gt;,
     *     email text,
     *     location text,
     *     twitter_username text
     * );
     * </pre>
     */
    default void createContactInfoUDT(@NotNull final CqlSession cqlSession) {
        createSpeakerCompanyUDT(cqlSession);
        cqlSession.execute(createType(CONTACT_INFO_UDT).ifNotExists()
                .withField(CONTACT_INFO_ATT_COMPANIES, DataTypes.setOf(udt(SPEAKER_COMPANY_UDT, true)))
                .withField(CONTACT_INFO_ATT_EMAIL, DataTypes.TEXT)
                .withField(CONTACT_INFO_ATT_LOCATION, DataTypes.TEXT)
                .withField(CONTACT_INFO_ATT_TWITTER, DataTypes.TEXT)
                .build());
    }

    /**
     * Create the <i>speaker_short_info</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS speaker_short_info (
     *     id uuid,
     *     contact_info frozen&lt;contact_info&gt;,
     *     name text
     * );
     * </pre>
     */
    default void createSpeakerShortInfoUDT(@NotNull final CqlSession cqlSession) {
        createContactInfoUDT(cqlSession);
        cqlSession.execute(createType(SPEAKER_SHORT_INFO_UDT).ifNotExists()
                .withField(SPEAKER_ATT_ID, DataTypes.UUID)
                .withField(SPEAKER_ATT_CONTACT_INFO, udt(CONTACT_INFO_UDT, true))
                .withField(SPEAKER_ATT_NAME, DataTypes.TEXT)
                .build());
    }

    /**
     * Create the <i>speaker_full_info</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS speaker_full_info (
     *     id uuid,
     *     avatar text,
     *     bio text,
     *     contact_info frozen&lt;contact_info&gt;,
     *     name text
     * );
     * </pre>
     */
    default void createSpeakerFullInfoUDT(@NotNull final CqlSession cqlSession) {
        createContactInfoUDT(cqlSession);
        cqlSession.execute(createType(SPEAKER_FULL_INFO_UDT).ifNotExists()
                .withField(SPEAKER_ATT_ID, DataTypes.UUID)
                .withField(SPEAKER_ATT_AVATAR, DataTypes.TEXT)
                .withField(SPEAKER_ATT_BIO, DataTypes.TEXT)
                .withField(SPEAKER_ATT_CONTACT_INFO, udt(CONTACT_INFO_UDT, true))
                .withField(SPEAKER_ATT_NAME, DataTypes.TEXT)
                .build());
    }

    /**
     * Create the <i>complexity</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS complexity (
     *     value int,
     *     description text,
     * );
     * </pre>
     */
    default void createComplexityUDT(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createType(COMPLEXITY_UDT).ifNotExists()
                .withField(COMPLEXITY_UDT_ATT_VALUE, DataTypes.INT)
                .withField(COMPLEXITY_UDT_ATT_DESCRIPTION, DataTypes.TEXT)
                .build());
    }
}

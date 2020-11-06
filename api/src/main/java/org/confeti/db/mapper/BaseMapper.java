package org.confeti.db.mapper;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import org.jetbrains.annotations.NotNull;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createType;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static org.confeti.db.model.conference.ConferenceEntity.CONFERENCE_ATT_LOGO;
import static org.confeti.db.model.conference.ConferenceEntity.CONFERENCE_ATT_NAME;
import static org.confeti.db.model.conference.ConferenceEntity.CONFERENCE_ATT_YEAR;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_AVATAR;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_BIO;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_CONTACT_INFO;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_ID;
import static org.confeti.db.model.speaker.SpeakerEntity.SPEAKER_ATT_NAME;
import static org.confeti.db.model.udt.ConferenceShortInfoUDT.CONFERENCE_SHORT_INFO_UDT;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_COMPANIES;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_EMAIL;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_LOCATIONS;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_ATT_TWITTER_USERNAME;
import static org.confeti.db.model.udt.ContactInfoUDT.CONTACT_INFO_UDT;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_PRESENTATION_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_REPO_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_ATT_VIDEO_URL;
import static org.confeti.db.model.udt.ReportSourceUDT.REPORT_SOURCE_UDT;
import static org.confeti.db.model.udt.SpeakerCompanyUDT.SPEAKER_COMPANY_ATT_ADDED_DATE;
import static org.confeti.db.model.udt.SpeakerCompanyUDT.SPEAKER_COMPANY_ATT_NAME;
import static org.confeti.db.model.udt.SpeakerCompanyUDT.SPEAKER_COMPANY_UDT;
import static org.confeti.db.model.udt.SpeakerFullInfoUDT.SPEAKER_FULL_INFO_UDT;
import static org.confeti.db.model.udt.SpeakerLocationUDT.SPEAKER_LOCATION_ATT_ADDED_DATE;
import static org.confeti.db.model.udt.SpeakerLocationUDT.SPEAKER_LOCATION_ATT_NAME;
import static org.confeti.db.model.udt.SpeakerLocationUDT.SPEAKER_LOCATION_UDT;
import static org.confeti.db.model.udt.SpeakerShortInfoUDT.SPEAKER_SHORT_INFO_UDT;

public interface BaseMapper {

    /**
     * Create the <i>speaker_location</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS speaker_location (
     *     added_date timestamp,
     *     name text
     * );
     * </pre>
     */
    default void createSpeakerLocationUDT(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createType(SPEAKER_LOCATION_UDT).ifNotExists()
                .withField(SPEAKER_LOCATION_ATT_ADDED_DATE, DataTypes.TIMESTAMP)
                .withField(SPEAKER_LOCATION_ATT_NAME, DataTypes.TEXT)
                .build());
    }

    /**
     * Create the <i>speaker_company</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS speaker_company (
     *     added_date timestamp,
     *     name text
     * );
     * </pre>
     */
    default void createSpeakerCompanyUDT(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createType(SPEAKER_COMPANY_UDT).ifNotExists()
                .withField(SPEAKER_COMPANY_ATT_ADDED_DATE, DataTypes.TIMESTAMP)
                .withField(SPEAKER_COMPANY_ATT_NAME, DataTypes.TEXT)
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
     * );
     * </pre>
     */
    default void createReportSourceUDT(@NotNull final CqlSession cqlSession) {
        cqlSession.execute(createType(REPORT_SOURCE_UDT).ifNotExists()
                .withField(REPORT_SOURCE_ATT_PRESENTATION_URL, DataTypes.TEXT)
                .withField(REPORT_SOURCE_ATT_REPO_URL, DataTypes.TEXT)
                .withField(REPORT_SOURCE_ATT_VIDEO_URL, DataTypes.TEXT)
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
        cqlSession.execute(createType(CONFERENCE_SHORT_INFO_UDT).ifNotExists()
                .withField(CONFERENCE_ATT_LOGO, DataTypes.TEXT)
                .withField(CONFERENCE_ATT_NAME, DataTypes.TEXT)
                .withField(CONFERENCE_ATT_YEAR, DataTypes.INT)
                .build());
    }

    /**
     * Create the <i>contact_info</i> UDT.
     *
     * <pre>
     * CREATE TYPE IF NOT EXISTS contact_info (
     *     companies set&lt;frozen&lt;speaker_company&gt;&gt;,
     *     email text,
     *     locations set&lt;frozen&lt;speaker_location&gt;&gt;,
     *     twitter_username text
     * );
     * </pre>
     */
    default void createContactInfoUDT(@NotNull final CqlSession cqlSession) {
        createSpeakerCompanyUDT(cqlSession);
        createSpeakerLocationUDT(cqlSession);
        cqlSession.execute(createType(CONTACT_INFO_UDT).ifNotExists()
                .withField(CONTACT_INFO_ATT_COMPANIES, DataTypes.setOf(udt(SPEAKER_COMPANY_UDT, true)))
                .withField(CONTACT_INFO_ATT_EMAIL, DataTypes.TEXT)
                .withField(CONTACT_INFO_ATT_LOCATIONS, DataTypes.setOf(udt(SPEAKER_LOCATION_UDT, true)))
                .withField(CONTACT_INFO_ATT_TWITTER_USERNAME, DataTypes.TEXT)
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
}

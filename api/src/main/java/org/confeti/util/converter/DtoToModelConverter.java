package org.confeti.util.converter;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import org.confeti.db.model.BaseEntity;
import org.confeti.db.model.conference.AbstractConferenceEntity;
import org.confeti.db.model.conference.ConferenceBySpeakerEntity;
import org.confeti.db.model.conference.ConferenceEntity;
import org.confeti.db.model.report.AbstractReportEntity;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.db.model.speaker.AbstractSpeakerEntity;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.db.model.speaker.SpeakerEntity;
import org.confeti.db.model.udt.ConferenceShortInfoUDT;
import org.confeti.db.model.udt.ContactInfoUDT;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.confeti.db.model.udt.SpeakerCompanyUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.confeti.db.model.udt.SpeakerLocationUDT;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;
import org.confeti.handlers.dto.BaseDTO;
import org.confeti.handlers.dto.Conference;
import org.confeti.handlers.dto.Report;
import org.confeti.handlers.dto.Speaker;
import org.confeti.handlers.dto.Speaker.ContactInfo;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.confeti.util.converter.HelperConverter.mapSet;
import static org.confeti.util.converter.HelperConverter.mapValue;

public final class DtoToModelConverter {

    private DtoToModelConverter() {
        // not instantiable
    }

    @NotNull
    public static ConferenceEntity convert(@NotNull final Conference conference) {
        return (ConferenceEntity) convert(conference, ConferenceEntity.builder());
    }

    @NotNull
    public static ConferenceBySpeakerEntity convert(@NotNull final Conference conference,
                                                    @NotNull final UUID speakerId) {
        return (ConferenceBySpeakerEntity) convert(
                conference,
                ConferenceBySpeakerEntity.builder().speakerId(speakerId));
    }

    @NotNull
    private static AbstractConferenceEntity convert(@NotNull final Conference conference,
                                                    @NotNull final AbstractConferenceEntity.AbstractConferenceEntityBuilder<?, ?> builder) {
        return builder
                .name(conference.getName())
                .location(conference.getLocation())
                .logo(conference.getLogo())
                .url(conference.getUrl())
                .year(conference.getYear())
                .build();
    }

    @NotNull
    public static SpeakerEntity convert(@NotNull final Speaker speaker) {
        return (SpeakerEntity) convert(
                speaker,
                SpeakerEntity.builder()
                        .bio(speaker.getBio())
                        .contactInfo(convertToUDT(speaker.getContactInfo())));
    }

    @NotNull
    public static SpeakerByConferenceEntity convert(@NotNull final Speaker speaker,
                                                    @NotNull final String conferenceName,
                                                    @NotNull final Integer year) {
        return (SpeakerByConferenceEntity) convert(
                speaker,
                SpeakerByConferenceEntity.builder()
                        .conferenceName(conferenceName)
                        .year(year)
                        .locations(Sets.newHashSet(SpeakerLocationUDT.builder()
                                .addedDate(Instant.now())
                                .name(speaker.getContactInfo().getLocation())
                                .build())));
    }

    @NotNull
    private static AbstractSpeakerEntity convert(@NotNull final Speaker speaker,
                                                 @NotNull final AbstractSpeakerEntity.AbstractSpeakerEntityBuilder<?, ?> builder) {
        return builder
                .id(speaker.getId())
                .avatar(speaker.getAvatar())
                .name(speaker.getName())
                .build();
    }

    @NotNull
    public static ReportEntity convert(@NotNull final Report report) {
        return (ReportEntity) convert(
                report,
                ReportEntity.builder()
                        .conferences(mapSet(report.getConferences(), DtoToModelConverter::convertToShortUDT))
                        .description(report.getDescription())
                        .speakers(mapSet(report.getSpeakers(), DtoToModelConverter::convertToFullUDT))
                        .tags(report.getTags()));
    }

    @NotNull
    public static ReportByConferenceEntity convert(@NotNull final Report report,
                                                   @NotNull final String conferenceName,
                                                   @NotNull final Integer year) {
        return (ReportByConferenceEntity) convert(
                report,
                ReportByConferenceEntity.builder()
                        .conferenceName(conferenceName)
                        .year(year)
                        .speakers(mapSet(report.getSpeakers(), DtoToModelConverter::convertToShortUDT))
                        .tags(report.getTags()));
    }

    @NotNull
    public static ReportBySpeakerEntity convert(@NotNull final Report report,
                                                @NotNull final UUID speakerId,
                                                @NotNull final Integer year) {
        return (ReportBySpeakerEntity) convert(
                report,
                ReportBySpeakerEntity.builder()
                        .speakerId(speakerId)
                        .year(year)
                        .conferences(mapSet(report.getConferences(), DtoToModelConverter::convertToShortUDT))
                        .description(report.getDescription())
                        .tags(report.getTags()));
    }

    @NotNull
    public static ReportByTagEntity convert(@NotNull final Report report,
                                            @NotNull final String tagName) {
        return (ReportByTagEntity) convert(
                report,
                ReportByTagEntity.builder()
                        .tagName(tagName)
                        .conferences(mapSet(report.getConferences(), DtoToModelConverter::convertToShortUDT))
                        .speakers(mapSet(report.getSpeakers(), DtoToModelConverter::convertToShortUDT)));
    }

    @NotNull
    private static AbstractReportEntity convert(@NotNull final Report report,
                                                @NotNull final AbstractReportEntity.AbstractReportEntityBuilder<?, ?> builder) {
        return builder
                .id(report.getId())
                .title(report.getTitle())
                .complexity(mapValue(report.getComplexity(), Report.Complexity::getValue))
                .language(report.getLanguage())
                .source(mapValue(report.getSource(), DtoToModelConverter::convertToUDT))
                .build();
    }

    @NotNull
    public static ContactInfoUDT convertToUDT(@NotNull final ContactInfo contactInfo) {
        return ContactInfoUDT.builder()
                .email(contactInfo.getEmail())
                .twitterUsername(contactInfo.getTwitterUsername())
                .companies(Sets.newHashSet(SpeakerCompanyUDT.builder()
                        .addedDate(Instant.now())
                        .name(contactInfo.getCompany())
                        .year(LocalDate.now().getYear())
                        .build()))
                .locations(Sets.newHashSet(SpeakerLocationUDT.builder()
                        .addedDate(Instant.now())
                        .name(contactInfo.getLocation())
                        .build()))
                .build();
    }

    @NotNull
    public static ReportSourceUDT convertToUDT(@NotNull final Report.ReportSource entity) {
        return ReportSourceUDT.builder()
                .presentationUrl(entity.getPresentation())
                .repoUrl(entity.getRepo())
                .videoUrl(entity.getVideo())
                .build();
    }

    @NotNull
    public static ConferenceShortInfoUDT convertToShortUDT(@NotNull final Conference conference) {
        return ConferenceShortInfoUDT.builder()
                .name(conference.getName())
                .year(conference.getYear())
                .logo(conference.getLogo())
                .build();
    }

    @NotNull
    private static SpeakerShortInfoUDT convertToShortUDT(@NotNull final Speaker speaker) {
        return SpeakerShortInfoUDT.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .contactInfo(convertToUDT(speaker.getContactInfo()))
                .build();
    }

    @NotNull
    private static SpeakerFullInfoUDT convertToFullUDT(@NotNull final Speaker speaker) {
        return SpeakerFullInfoUDT.builder()
                .id(speaker.getId())
                .avatar(speaker.getAvatar())
                .bio(speaker.getBio())
                .name(speaker.getName())
                .contactInfo(convertToUDT(speaker.getContactInfo()))
                .build();
    }
}

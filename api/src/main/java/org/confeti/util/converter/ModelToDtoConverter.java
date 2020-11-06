package org.confeti.util.converter;

import org.confeti.db.model.conference.AbstractConferenceEntity;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.db.model.report.stats.AbstractReportStatsEntity;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.db.model.speaker.SpeakerEntity;
import org.confeti.db.model.udt.ConferenceShortInfoUDT;
import org.confeti.db.model.udt.ContactInfoUDT;
import org.confeti.db.model.udt.ReportSourceUDT;
import org.confeti.db.model.udt.SpeakerFullInfoUDT;
import org.confeti.db.model.udt.SpeakerShortInfoUDT;
import org.confeti.handlers.dto.Conference;
import org.confeti.handlers.dto.Report;
import org.confeti.handlers.dto.Report.ReportSource;
import org.confeti.handlers.dto.ReportStats;
import org.confeti.handlers.dto.Speaker;
import org.confeti.handlers.dto.Speaker.ContactInfo;
import org.jetbrains.annotations.NotNull;

public final class ModelToDtoConverter {

    private ModelToDtoConverter() {
        // not instantiable
    }

    @NotNull
    public static Conference convert(@NotNull final AbstractConferenceEntity entity) {
        return Conference.builder(entity.getName(), entity.getYear())
                .location(entity.getLocation())
                .logo(entity.getLogo())
                .url(entity.getUrl())
                .build();
    }

    @NotNull
    public static Conference convert(@NotNull final ConferenceShortInfoUDT entity) {
        return Conference.builder(entity.getName(), entity.getYear())
                .logo(entity.getLogo())
                .build();
    }

    @NotNull
    public static ContactInfo convert(final ContactInfoUDT entity) {
        return ContactInfo.builder()
                .email(entity.getEmail())
                .twitterUsername(entity.getTwitterUsername())
                .company(HelperConverter.getLastSpeakerCompany(entity.getCompanies()))
                .location(HelperConverter.getLastSpeakerLocation(entity.getLocations()))
                .build();
    }

    @NotNull
    public static Speaker convert(@NotNull final SpeakerEntity entity) {
        return Speaker.builder(entity.getId(), entity.getName())
                .avatar(entity.getAvatar())
                .bio(entity.getBio())
                .contactInfo(convert(entity.getContactInfo()))
                .build();
    }

    @NotNull
    public static Speaker convert(@NotNull final SpeakerByConferenceEntity entity) {
        return Speaker.builder(entity.getId(), entity.getName())
                .avatar(entity.getAvatar())
                .contactInfo(ContactInfo.builder()
                        .location(HelperConverter.getLastSpeakerLocation(entity.getLocations()))
                        .build())
                .build();
    }

    @NotNull
    public static Speaker convert(@NotNull final SpeakerShortInfoUDT entity) {
        return Speaker.builder(entity.getId(), entity.getName())
                .contactInfo(convert(entity.getContactInfo()))
                .build();
    }

    @NotNull
    public static Speaker convert(@NotNull final SpeakerFullInfoUDT entity) {
        return Speaker.builder(entity.getId(), entity.getName())
                .avatar(entity.getAvatar())
                .bio(entity.getBio())
                .contactInfo(convert(entity.getContactInfo()))
                .build();
    }

    @NotNull
    public static ReportSource convert(@NotNull final ReportSourceUDT entity) {
        return ReportSource.builder()
                .presentation(entity.getPresentationUrl())
                .video(entity.getVideoUrl())
                .repo(entity.getRepoUrl())
                .build();
    }

    @NotNull
    public static Report convert(@NotNull final ReportEntity entity) {
        return Report.builder(entity.getId(), entity.getTitle())
                .complexity(Report.Complexity.valueOf(entity.getComplexity()))
                .conferences(HelperConverter.mapSet(entity.getConferences(), ModelToDtoConverter::convert))
                .description(entity.getDescription())
                .language(entity.getLanguage())
                .source(convert(entity.getSource()))
                .speakers(HelperConverter.mapSet(entity.getSpeakers(), ModelToDtoConverter::convert))
                .tags(entity.getTags())
                .build();
    }

    @NotNull
    public static Report convert(@NotNull final ReportByConferenceEntity entity) {
        return Report.builder(entity.getId(), entity.getTitle())
                .complexity(Report.Complexity.valueOf(entity.getComplexity()))
                .language(entity.getLanguage())
                .source(convert(entity.getSource()))
                .speakers(HelperConverter.mapSet(entity.getSpeakers(), ModelToDtoConverter::convert))
                .tags(entity.getTags())
                .build();
    }

    @NotNull
    public static Report convert(@NotNull final ReportBySpeakerEntity entity) {
        return Report.builder(entity.getId(), entity.getTitle())
                .complexity(Report.Complexity.valueOf(entity.getComplexity()))
                .conferences(HelperConverter.mapSet(entity.getConferences(), ModelToDtoConverter::convert))
                .language(entity.getLanguage())
                .source(convert(entity.getSource()))
                .tags(entity.getTags())
                .build();
    }

    @NotNull
    public static Report convert(@NotNull final ReportByTagEntity entity) {
        return Report.builder(entity.getId(), entity.getTitle())
                .complexity(Report.Complexity.valueOf(entity.getComplexity()))
                .conferences(HelperConverter.mapSet(entity.getConferences(), ModelToDtoConverter::convert))
                .language(entity.getLanguage())
                .source(convert(entity.getSource()))
                .speakers(HelperConverter.mapSet(entity.getSpeakers(), ModelToDtoConverter::convert))
                .build();
    }

    @NotNull
    public static ReportStats convert(@NotNull final AbstractReportStatsEntity entity) {
        return new ReportStats(entity.getReportTotal());
    }
}

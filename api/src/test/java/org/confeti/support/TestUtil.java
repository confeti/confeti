package org.confeti.support;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import org.apache.commons.lang.RandomStringUtils;
import org.confeti.service.dto.Company;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestUtil {

    private static final String URL_PREFIX = "https://";

    private static final String SPEAKER_PREFIX = "speaker";
    private static final String SPEAKER_AVATAR_PREFIX = "avatar";
    private static final String SPEAKER_BIO_PREFIX = "bio";

    private static final String CONTACT_INFO_COMPANY_PREFIX = "company";
    private static final String CONTACT_INFO_EMAIL_SUFFIX = "@gmail.com";
    private static final String CONTACT_INFO_LOCATION_PREFIX = "location";
    private static final String CONTACT_INFO_TWITTER_USERNAME = "username";

    private static final String COMPANY_PREFIX = "company";
    private static final String COMPANY_LOGO_PREFIX = "logo";

    private static final String CONFERENCE_PREFIX = "conference";
    private static final String CONFERENCE_LOCATION_PREFIX = "location";
    private static final String CONFERENCE_LOGO_PREFIX = "logo";

    private static final String REPORT_PREFIX = "report";
    private static final String REPORT_DESCRIPTION_PREFIX = "description";
    private static final String REPORT_SOURCE_PRESENTATION_PREFIX = URL_PREFIX + "presentation/";
    private static final String REPORT_SOURCE_VIDEO_PREFIX = URL_PREFIX + "video/";
    private static final String REPORT_SOURCE_REPO_PREFIX = URL_PREFIX + "repo/";
    private static final String REPORT_SOURCE_TALK_PREFIX = URL_PREFIX + "talk/";
    private static final String REPORT_SOURCE_ARTICLE_PREFIX = URL_PREFIX + "article/";
    private static final String REPORT_TAG_PREFIX = "tag";

    private static final List<String> LANGUAGES = List.of("ru", "en");

    private static final int minYear = 2013;
    private static final int maxYear = 2020;

    private static long speakerSeqNum = 0L;
    private static long companySeqNum = 0L;
    private static long conferenceSeqNum = 0L;
    private static long reportSeqNum = 0L;
    private static long tagSeqNum = 0L;

    private static long nextSpeakerNum() {
        return ++speakerSeqNum;
    }

    private static long nextCompanyNum() {
        return ++companySeqNum;
    }

    private static long nextConferenceNum() {
        return ++conferenceSeqNum;
    }

    private static long nextReportNum() {
        return ++reportSeqNum;
    }

    private static long nextTagNum() {
        return ++tagSeqNum;
    }

    private TestUtil() {
        // not instantiable
    }

    public static int generateYear() {
        return ThreadLocalRandom.current().nextInt(maxYear - minYear) + minYear;
    }

    @NotNull
    public static String generateLanguage() {
        return LANGUAGES.get(ThreadLocalRandom.current().nextInt(LANGUAGES.size()));
    }

    @NotNull
    public static String updateLanguage(@NotNull final String language) {
        return LANGUAGES.get((LANGUAGES.indexOf(language) + 1) % LANGUAGES.size());
    }

    @NotNull
    public static String generateTag() {
        return REPORT_TAG_PREFIX + nextTagNum();
    }

    @NotNull
    public static String updateTag(@NotNull final String tagName) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return tagName + randomString;
    }

    @NotNull
    public static Report.Complexity generateComplexity() {
        return Report.Complexity.builder()
                .value(ThreadLocalRandom.current().nextInt(0, 5))
                .description(RandomStringUtils.randomAlphabetic(5))
                .build();
    }

    @NotNull
    public static Report.Complexity updateComplexity(@NotNull final Report.Complexity complexity) {
        return Report.Complexity.builder()
                .value(complexity.getValue() - 1)
                .description(complexity.getDescription() + RandomStringUtils.randomAlphabetic(5))
                .build();
    }

    @NotNull
    public static Report.ReportSource generateReportSource(@NotNull final String reportTitle) {
        return Report.ReportSource.builder()
                .presentation(REPORT_SOURCE_PRESENTATION_PREFIX + reportTitle)
                .video(REPORT_SOURCE_VIDEO_PREFIX + reportTitle)
                .repo(REPORT_SOURCE_REPO_PREFIX + reportTitle)
                .talk(REPORT_SOURCE_TALK_PREFIX + reportTitle)
                .article(REPORT_SOURCE_ARTICLE_PREFIX + reportTitle)
                .build();
    }

    @NotNull
    public static Report.ReportSource updateReportSource(@NotNull final Report.ReportSource source) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Report.ReportSource.builder()
                .presentation(source.getPresentation() + randomString)
                .video(source.getVideo() + randomString)
                .repo(source.getRepo() + randomString)
                .talk(source.getTalk() + randomString)
                .article(source.getArticle() + randomString)
                .build();
    }

    @NotNull
    public static Speaker.ContactInfo.SpeakerCompany generateSpeakerCompany() {
        final var company = generateCompany();
        return Speaker.ContactInfo.SpeakerCompany.builder()
                .addedDate(Instant.ofEpochMilli(Instant.now().toEpochMilli()))
                .name(company.getName())
                .year(generateYear())
                .build();
    }

    @NotNull
    public static Speaker.ContactInfo.SpeakerCompany updateSpeakerCompany(@NotNull final Speaker speaker) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Speaker.ContactInfo.SpeakerCompany.builder()
                .addedDate(Instant.ofEpochMilli(Instant.now().toEpochMilli()))
                .name(speaker.getContactInfo().getCompany().getName() + randomString)
                .year(speaker.getContactInfo().getCompany().getYear() + 1)
                .build();
    }

    @NotNull
    public static Speaker.ContactInfo generateContactInfo(@NotNull final String speakerName,
                                                          final long speakerNum) {
        return Speaker.ContactInfo.builder()
                .company(generateSpeakerCompany())
                .email(speakerName + CONTACT_INFO_EMAIL_SUFFIX)
                .location(CONTACT_INFO_LOCATION_PREFIX + speakerNum)
                .twitterUsername(CONTACT_INFO_TWITTER_USERNAME + speakerNum)
                .build();
    }

    @NotNull
    public static Speaker.ContactInfo updateContactInfo(@NotNull final Speaker speaker,
                                                        final boolean needUpdateEmail,
                                                        final boolean needUpdateTwitter) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        final var oldEmail = speaker.getContactInfo().getEmail();
        final var oldTwitterUsername = speaker.getContactInfo().getTwitterUsername();
        final var email = needUpdateEmail ? randomString + oldEmail : oldEmail;
        final var twitterUsername = needUpdateTwitter ? oldTwitterUsername + randomString : oldTwitterUsername;
        return Speaker.ContactInfo.builder()
                .company(updateSpeakerCompany(speaker))
                .email(email)
                .location(speaker.getContactInfo().getLocation() + randomString)
                .twitterUsername(twitterUsername)
                .build();
    }

    @NotNull
    public static Speaker generateSpeaker() {
        final long num = nextSpeakerNum();
        final var name = SPEAKER_PREFIX + num;
        return Speaker.builder(name)
                .bio(SPEAKER_BIO_PREFIX + num)
                .avatar(SPEAKER_AVATAR_PREFIX + num)
                .contactInfo(generateContactInfo(name, num))
                .build();
    }

    @NotNull
    public static Speaker updateSpeaker(@NotNull final Speaker speaker) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Speaker.builder(speaker.getId(), speaker.getName())
                .bio(speaker.getBio() + randomString)
                .avatar(speaker.getAvatar() + randomString)
                .contactInfo(updateContactInfo(speaker, false, false))
                .build();
    }

    @NotNull
    public static Speaker updateSpeaker(@NotNull final Speaker speaker,
                                        final boolean needUpdateEmail,
                                        final boolean needUpdateTwitter) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Speaker.builder(speaker.getId(), speaker.getName())
                .bio(speaker.getBio() + randomString)
                .avatar(speaker.getAvatar() + randomString)
                .contactInfo(updateContactInfo(speaker, needUpdateEmail, needUpdateTwitter))
                .build();
    }

    @NotNull
    public static Company generateCompany() {
        final long num = nextCompanyNum();
        final var name = COMPANY_PREFIX + num;
        return Company.builder(name)
                .logo(name + COMPANY_LOGO_PREFIX + num)
                .build();
    }

    public static Company updateCompany(@NotNull final Company company) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Company.builder(company.getName())
                .logo(company.getLogo() + randomString)
                .build();
    }

    @NotNull
    public static Conference generateConference() {
        final long num = nextConferenceNum();
        final var name = CONFERENCE_PREFIX + num;
        return Conference.builder(name, generateYear())
                .url(URL_PREFIX + name)
                .logo(name + CONFERENCE_LOGO_PREFIX + num)
                .location(CONFERENCE_LOCATION_PREFIX + num)
                .build();
    }

    @NotNull
    public static Conference updateConference(@NotNull final Conference conference) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Conference.builder(conference.getName(), conference.getYear())
                .url(conference.getUrl() + randomString)
                .logo(conference.getLogo() + randomString)
                .location(conference.getLocation() + randomString)
                .build();
    }

    @NotNull
    public static Report generateReport(final int conferenceNum,
                                        final int speakerNum,
                                        final int tagNum) {
        final long num = nextReportNum();
        final var title = REPORT_PREFIX + num;
        return Report.builder(title)
                .complexity(generateComplexity())
                .language(generateLanguage())
                .source(generateReportSource(title))
                .description(REPORT_DESCRIPTION_PREFIX + title)
                .tags(Stream.generate(TestUtil::generateTag)
                        .limit(tagNum)
                        .collect(Collectors.toSet()))
                .conferences(Stream.generate(TestUtil::generateConference)
                        .limit(conferenceNum)
                        .collect(Collectors.toSet()))
                .speakers(Stream.generate(TestUtil::generateSpeaker)
                        .limit(speakerNum)
                        .collect(Collectors.toSet()))
                .build();
    }

    @NotNull
    public static Report updateReport(@NotNull final Report report,
                                      final boolean needUpdateConferences,
                                      final boolean needUpdateSpeakers) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Report.builder(report.getId(), report.getTitle())
                .complexity(updateComplexity(report.getComplexity()))
                .language(updateLanguage(report.getLanguage()))
                .source(updateReportSource(report.getSource()))
                .description(report.getDescription() + randomString)
                .tags(report.getTags().stream()
                        .map(TestUtil::updateTag)
                        .collect(Collectors.toSet()))
                .conferences(needUpdateConferences
                        ? report.getConferences().stream()
                        .map(TestUtil::updateConference)
                        .collect(Collectors.toSet())
                        : Sets.newHashSet(report.getConferences()))
                .speakers(needUpdateSpeakers
                        ? report.getSpeakers().stream()
                        .map(TestUtil::updateSpeaker)
                        .collect(Collectors.toSet())
                        : Sets.newHashSet(report.getSpeakers()))
                .build();
    }
}

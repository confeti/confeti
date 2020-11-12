package org.confeti.support;

import org.apache.commons.lang.RandomStringUtils;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Speaker;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class TestUtil {

    private static final String SPEAKER_PREFIX = "speaker";
    private static final String SPEAKER_AVATAR_PREFIX = "avatar";
    private static final String SPEAKER_BIO_PREFIX = "bio";

    private static final String CONTACT_INFO_COMPANY_PREFIX = "company";
    private static final String CONTACT_INFO_EMAIL_SUFFIX = "@gmail.com";
    private static final String CONTACT_INFO_LOCATION_PREFIX = "location";
    private static final String CONTACT_INFO_TWITTER_USERNAME = "username";

    private static final String CONFERENCE_PREFIX = "conference";
    private static final String CONFERENCE_LOCATION_PREFIX = "location";
    private static final String CONFERENCE_LOGO_PREFIX = "logo";
    private static final String CONFERENCE_URL_PREFIX = "https://";

    private static final int minYear = 2013;
    private static final int maxYear = 2020;


    private static long speakerNum = 0L;
    private static long conferenceNum = 0L;

    private static long nextSpeakerNum() {
        return ++speakerNum;
    }
    private static long nextConferenceNum() {
        return ++conferenceNum;
    }

    private TestUtil() {
        // not instantiable
    }

    public static int generateYear() {
        return ThreadLocalRandom.current().nextInt(maxYear - minYear) + minYear;
    }

    @NotNull
    public static Speaker.ContactInfo.SpeakerCompany generateSpeakerCompany(final long speakerNum) {
       return Speaker.ContactInfo.SpeakerCompany.builder()
               .addedDate(Instant.ofEpochMilli(Instant.now().toEpochMilli()))
               .name(CONTACT_INFO_COMPANY_PREFIX + speakerNum)
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
                .company(generateSpeakerCompany(speakerNum))
                .email(speakerName + CONTACT_INFO_EMAIL_SUFFIX)
                .location(CONTACT_INFO_LOCATION_PREFIX + speakerNum)
                .twitterUsername(CONTACT_INFO_TWITTER_USERNAME + speakerNum)
                .build();
    }

    @NotNull
    public static Speaker.ContactInfo updateContactInfo(@NotNull final Speaker speaker) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Speaker.ContactInfo.builder()
                .company(updateSpeakerCompany(speaker))
                .email(speaker.getContactInfo().getEmail())
                .location(speaker.getContactInfo().getLocation() + randomString)
                .twitterUsername(speaker.getContactInfo().getTwitterUsername())
                .build();
    }

    @NotNull
    public static Speaker generateSpeaker() {
        final long num = nextSpeakerNum();
        final var name = SPEAKER_PREFIX + num;
        return Speaker.builder(UUID.randomUUID(), name)
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
                .contactInfo(updateContactInfo(speaker))
                .build();
    }

    @NotNull
    public static Conference generateConference() {
        final long num = nextConferenceNum();
        final var name = CONFERENCE_PREFIX + num;
        return Conference.builder(name, generateYear())
                .url(CONFERENCE_URL_PREFIX + name)
                .logo(CONFERENCE_LOGO_PREFIX + num)
                .location(CONFERENCE_LOCATION_PREFIX + num)
                .build();
    }

    @NotNull
    public static Conference updateConference(@NotNull final Conference conference) {
        final var randomString = RandomStringUtils.randomAlphabetic(5);
        return Conference.builder(conference.getName(), conference.getYear())
                .url(conference.getUrl() + randomString)
                .logo(conference.getLogo() + randomString)
                .location(conference.getLocation())
                .build();
    }
}

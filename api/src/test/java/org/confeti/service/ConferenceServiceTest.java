package org.confeti.service;

import org.confeti.db.model.conference.ConferenceBySpeakerEntity;
import org.confeti.service.dto.Conference;
import org.confeti.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.confeti.support.TestUtil.generateConference;
import static org.confeti.support.TestUtil.generateSpeaker;
import static org.confeti.support.TestUtil.updateConference;

public class ConferenceServiceTest extends AbstractIntegrationTest {

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private SpeakerService speakerService;

    @Test
    public void insertConferenceTest() {
        final var conference = generateConference();

        StepVerifier.create(conferenceService.upsert(conference))
                .expectNext(conference)
                .expectComplete()
                .verify();
    }

    @Test
    public void insertConferenceWhenInsertConferenceBySpeakerTest() {
        final var conference = generateConference();
        final var speaker = generateSpeaker();
        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference, speaker.getId()).block();

        StepVerifier.create(conferenceService.findBy(conference.getName()))
                .expectNext(conference)
                .expectComplete()
                .verify();
    }

    @Test
    public void insertConferenceBySpeakerTest() {
        final var conference = generateConference();
        final var speaker = generateSpeaker();
        speakerService.upsert(speaker).block();

        StepVerifier.create(conferenceService.upsert(conference, speaker.getId()))
                .expectNext(conference)
                .expectComplete()
                .verify();
    }

    @Test
    public void insertConferenceBySpeakerButSpeakerNotExistTest() {
        final var conference = generateConference();
        final var speaker = generateSpeaker();
        conferenceService.upsert(conference, speaker.getId()).block();

        StepVerifier.create(conferenceService.findBy(conference.getName()))
                .expectNext(conference)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    public void updateConferenceTest() {
        final var conference = generateConference();
        final var updatedConference = updateConference(conference);
        conferenceService.upsert(conference).block();

        StepVerifier.create(conferenceService.upsert(updatedConference))
                .expectNext(updatedConference)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference.getName()))
                .expectNext(updatedConference)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateConferenceBySpeakerTest() {
        final var conference = generateConference();
        final var updatedConference = updateConference(conference);
        final var speaker = generateSpeaker();
        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference, speaker.getId()).block();

        StepVerifier.create(conferenceService.upsert(updatedConference, speaker.getId()))
                .expectNext(updatedConference)
                .expectComplete()
                .verify();

        final var expectedConference = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), updatedConference));

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectNext(expectedConference)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateConferenceWhenUpdateConferenceBySpeakerTest() {
        final var conference = generateConference();
        final var updatedConference = updateConference(conference);
        final var speaker = generateSpeaker();
        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference, speaker.getId()).block();
        conferenceService.upsert(updatedConference, speaker.getId()).block();

        StepVerifier.create(conferenceService.findBy(conference.getName(), conference.getYear()))
                .expectNext(updatedConference)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateConferenceBySpeakerWhenUpdateConferenceTest() {
        final var conference = generateConference();
        final var updatedConference = updateConference(conference);
        final var speaker = generateSpeaker();
        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();
        conferenceService.upsert(conference, speaker.getId()).block();
        conferenceService.upsert(updatedConference).block();

        final var expectedConference = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), updatedConference));

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectNext(expectedConference)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferenceByNameTest() {
        final var conference = generateConference();
        conferenceService.upsert(conference).block();

        StepVerifier.create(conferenceService.findBy(conference.getName()))
                .expectNext(conference)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndSameYearByNameTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName()))
                .expectNext(conference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndDiffYearsByNameTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName()))
                .expectNext(conference2)
                .expectNext(conference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndSameYearByNameTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conference2.setYear(conference1.getYear());
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName()))
                .expectNext(conference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference2.getName()))
                .expectNext(conference2)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndDiffYearsByNameTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conference2.setYear(conference1.getYear() + 1);
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName()))
                .expectNext(conference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference2.getName()))
                .expectNext(conference2)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferenceByNameAndYearTest() {
        final var conference = generateConference();
        conferenceService.upsert(conference).block();

        StepVerifier.create(conferenceService.findBy(conference.getName(), conference.getYear()))
                .expectNext(conference)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndSameYearByNameAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(conference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndDiffYearsByNameAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(conference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference1.getName(), conference2.getYear()))
                .expectNext(conference2)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndSameYearByNameAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conference2.setYear(conference1.getYear());
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(conference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference2.getName(), conference1.getYear()))
                .expectNext(conference2)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndDiffYearsByNameAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();

        StepVerifier.create(conferenceService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(conference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference2.getName(), conference2.getYear()))
                .expectNext(conference2)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferenceBySpeakerIdTest() {
        final var conference = generateConference();
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference, speaker.getId()).block();

        final var expectedConference = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference));

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectNext(expectedConference)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndSameYearsBySpeakerIdTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference1, speaker.getId()).block();
        conferenceService.upsert(conference2, speaker.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectNext(expectedConference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndDiffYearsBySpeakerIdTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference1, speaker.getId()).block();
        conferenceService.upsert(conference2, speaker.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));
        final var expectedConference2 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference2));

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectNext(expectedConference2)
                .expectNext(expectedConference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndSameYearBySpeakerIdTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conference2.setYear(conference1.getYear());
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference1, speaker.getId()).block();
        conferenceService.upsert(conference2, speaker.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));
        final var expectedConference2 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference2));

        final boolean isConference1First = conference1.getName().compareTo(conference2.getName()) < 0;

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectNext(isConference1First ? expectedConference1 : expectedConference2)
                .expectNext(isConference1First ? expectedConference2 : expectedConference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndDiffYearsBySpeakerIdTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conference2.setYear(conference1.getYear() + 1);
        final var speaker1 = generateSpeaker();

        speakerService.upsert(speaker1).block();
        conferenceService.upsert(conference1, speaker1.getId()).block();
        conferenceService.upsert(conference2, speaker1.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker1.getId(), conference1));
        final var expectedConference2 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker1.getId(), conference2));

        StepVerifier.create(conferenceService.findBy(speaker1.getId()))
                .expectNext(expectedConference2)
                .expectNext(expectedConference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferenceBySpeakerIdAndYearTest() {
        final var conference = generateConference();
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference, speaker.getId()).block();

        final var expectedConference = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference));

        StepVerifier.create(conferenceService.findBy(speaker.getId(), conference.getYear()))
                .expectNext(expectedConference)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndSameYearBySpeakerIdAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference1, speaker.getId()).block();
        conferenceService.upsert(conference2, speaker.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));

        StepVerifier.create(conferenceService.findBy(speaker.getId(), conference1.getYear()))
                .expectNext(expectedConference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithSameNameAndDiffYearsBySpeakerIdAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference1, speaker.getId()).block();
        conferenceService.upsert(conference2, speaker.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));

        StepVerifier.create(conferenceService.findBy(speaker.getId(), conference1.getYear()))
                .expectNext(expectedConference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndSameYearBySpeakerIdAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conference2.setYear(conference1.getYear());
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference1, speaker.getId()).block();
        conferenceService.upsert(conference2, speaker.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));
        final var expectedConference2 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference2));

        final boolean isConference1First = conference1.getName().compareTo(conference2.getName()) < 0;

        StepVerifier.create(conferenceService.findBy(speaker.getId(), conference1.getYear()))
                .expectNext(isConference1First ? expectedConference1 : expectedConference2)
                .expectNext(isConference1First ? expectedConference2 : expectedConference1)
                .expectComplete()
                .verify();
    }

    @Test
    public void findConferencesWithDiffNamesAndDiffYearsBySpeakerIdAndYearTest() {
        final var conference1 = generateConference();
        final var conference2 = generateConference();
        conference2.setYear(conference1.getYear() + 1);
        final var speaker = generateSpeaker();

        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference1, speaker.getId()).block();
        conferenceService.upsert(conference2, speaker.getId()).block();

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));
        final var expectedConference2 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference2));

        StepVerifier.create(conferenceService.findBy(speaker.getId(), conference1.getYear()))
                .expectNext(expectedConference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(speaker.getId(), conference2.getYear()))
                .expectNext(expectedConference2)
                .expectComplete()
                .verify();
    }
}
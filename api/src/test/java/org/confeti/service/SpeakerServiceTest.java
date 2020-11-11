package org.confeti.service;

import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.service.dto.Speaker;
import org.confeti.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.confeti.support.TestUtil.cloneConference;
import static org.confeti.support.TestUtil.generateConference;
import static org.confeti.support.TestUtil.generateSpeaker;
import static org.confeti.support.TestUtil.updateSpeaker;

public class SpeakerServiceTest extends AbstractIntegrationTest {

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private ConferenceService conferenceService;

    @Test
    public void insertSpeakerTest() {
        final var speaker = generateSpeaker();

        StepVerifier.create(speakerService.upsert(speaker))
                .expectNext(speaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void insertSpeakerWhenInsertSpeakerByConferenceTest() {
        final var speaker = generateSpeaker();
        final var conference = generateConference();
        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(speaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void findSpeakerByIdTest() {
        final var speaker = generateSpeaker();
        speakerService.upsert(speaker).block();

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(speaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateSpeakerTest() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker = updateSpeaker(speaker);
        speakerService.upsert(speaker).block();

        StepVerifier.create(speakerService.upsert(updatedSpeaker))
                .expectNext(updatedSpeaker)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(updatedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void insertSpeakerByConferenceTest() {
        final var speaker = generateSpeaker();
        final var conference = generateConference();
        conferenceService.upsert(conference).block();

        StepVerifier.create(speakerService.upsert(speaker, conference.getName(), conference.getYear()))
                .expectNext(speaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void insertSpeakerByConferenceButConferenceNotExistTest() {
        final var speaker = generateSpeaker();
        final var conference = generateConference();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(speaker)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(conference.getName()))
                .expectComplete()
                .verify();
    }

    @Test
    public void findSpeakersByConferenceNameTest() {
        final var speaker1 = generateSpeaker();
        speaker1.setName("abc");
        final var speaker2 = generateSpeaker();
        speaker2.setName("bac");
        final var conference1 = generateConference();
        final var conference2 = generateConference();

        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();
        speakerService.upsert(speaker1, conference1.getName(), conference1.getYear()).block();
        speakerService.upsert(speaker2, conference1.getName(), conference1.getYear()).block();
        speakerService.upsert(speaker2, conference2.getName(), conference2.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference1.getName()))
                .expectNext(expectedSpeaker1)
                .expectNext(expectedSpeaker2)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(conference2.getName()))
                .expectNext(expectedSpeaker2)
                .expectComplete()
                .verify();
    }

    @Test
    public void findSpeakersByConferenceNameAndYearTest() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        final var conference2013 = generateConference();
        conference2013.setYear(2013);
        final var conference2014 = cloneConference(conference2013);
        conference2014.setYear(2014);

        conferenceService.upsert(conference2013).block();
        conferenceService.upsert(conference2014).block();
        speakerService.upsert(speaker1, conference2013.getName(), conference2013.getYear()).block();
        speakerService.upsert(speaker2, conference2013.getName(), conference2013.getYear()).block();
        speakerService.upsert(speaker2, conference2014.getName(), conference2014.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference2013.getName(), conference2013.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference2013.getName(), conference2013.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference2013.getName(), conference2013.getYear()))
                .expectNext(expectedSpeaker1)
                .expectNext(expectedSpeaker2)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(conference2014.getName(), conference2014.getYear()))
                .expectNext(expectedSpeaker2)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateSpeakerByConferenceTest() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker = updateSpeaker(speaker);
        final var conference = generateConference();
        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();

        StepVerifier.create(speakerService.upsert(updatedSpeaker, conference.getName(), conference.getYear()))
                .expectNext(updatedSpeaker)
                .expectComplete()
                .verify();

        final var expectedUpdatedSpeaker = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), updatedSpeaker));

        StepVerifier.create(speakerService.findBy(conference.getName()))
                .expectNext(expectedUpdatedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateSpeakerWhenUpdateSpeakerByConferenceTest() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker = updateSpeaker(speaker);
        final var conference = generateConference();
        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();
        speakerService.upsert(updatedSpeaker, conference.getName(), conference.getYear()).block();

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(updatedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateSpeakerByConferenceWhenUpdateSpeakerTest() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker = updateSpeaker(speaker);
        final var conference = generateConference();
        speakerService.upsert(speaker).block();
        conferenceService.upsert(conference, speaker.getId()).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();
        speakerService.upsert(updatedSpeaker).block();

        final var expectedSpeaker = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), updatedSpeaker));

        StepVerifier.create(speakerService.findBy(conference.getName()))
                .expectNext(expectedSpeaker)
                .expectComplete()
                .verify();
    }
}

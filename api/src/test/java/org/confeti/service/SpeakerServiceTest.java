package org.confeti.service;

import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Speaker;
import org.confeti.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.confeti.support.TestUtil.generateConference;
import static org.confeti.support.TestUtil.generateSpeaker;
import static org.confeti.support.TestUtil.updateSpeaker;

public class SpeakerServiceTest extends AbstractIntegrationTest {

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private ConferenceService conferenceService;

    @Test
    public void testInsertSpeaker() {
        final var speaker = generateSpeaker();

        StepVerifier.create(speakerService.upsert(speaker))
                .expectNext(speaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertSpeakerWhenInsertSpeakerByConference() {
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
    public void testInsertSpeakerByConference() {
        final var speaker = generateSpeaker();
        final var conference = generateConference();
        conferenceService.upsert(conference).block();

        StepVerifier.create(speakerService.upsert(speaker, conference.getName(), conference.getYear()))
                .expectNext(speaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertSpeakerByConferenceButConferenceNotExist() {
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
    public void testUpdateSpeaker() {
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
    public void testUpdateSpeakerWithSavingInfoAboutFormerCompany() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker = updateSpeaker(speaker);
        updatedSpeaker.getContactInfo().getCompany().setYear(
                speaker.getContactInfo().getCompany().getYear() - 1);
        speakerService.upsert(speaker).block();

        final var expectedSpeaker = Speaker.from(updatedSpeaker);
        expectedSpeaker.getContactInfo().setCompany(speaker.getContactInfo().getCompany());

        StepVerifier.create(speakerService.upsert(updatedSpeaker))
                .expectNext(expectedSpeaker)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(expectedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdateSpeakerByConference() {
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
    public void testUpdateSpeakerWhenUpdateSpeakerByConference() {
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
    public void testUpdateSpeakerWithSavingInfoAboutFormerCompanyWhenUpdateSpeakerByConference() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker = updateSpeaker(speaker);
        updatedSpeaker.getContactInfo().getCompany().setYear(
                speaker.getContactInfo().getCompany().getYear() - 1);
        final var conference = generateConference();
        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();
        speakerService.upsert(updatedSpeaker, conference.getName(), conference.getYear()).block();

        final var expectedSpeaker = Speaker.from(updatedSpeaker);
        expectedSpeaker.getContactInfo().setCompany(speaker.getContactInfo().getCompany());

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(expectedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdateSpeakerByConferenceWhenUpdateSpeaker() {
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

    @Test
    public void testUpdateSpeakerWhenExist2SpeakersInDbWithSameName() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = updateSpeaker(speaker1, true, true);
        final var updatedSpeaker2 = updateSpeaker(speaker2);
        speakerService.upsert(speaker1).block();
        speakerService.upsert(speaker2).block();
        speakerService.upsert(updatedSpeaker2).block();

        StepVerifier.create(speakerService.findByName(speaker1.getName()))
                .expectNextMatches(sp -> sp.equals(speaker1) || sp.equals(updatedSpeaker2))
                .expectNextMatches(sp -> sp.equals(speaker1) || sp.equals(updatedSpeaker2))
                .expectComplete()
                .verify();
    }

    @Test
    public void testNotUpdateSpeakerWhenSpeakersHaveSameNameButDiffTwitterUsernamesAndDiffEmails() {
        final var speaker = generateSpeaker();
        final var newSpeaker = updateSpeaker(speaker, true, true);
        speakerService.upsert(speaker).block();
        speakerService.upsert(newSpeaker).block();

        StepVerifier.create(speakerService.findByName(speaker.getName()))
                .expectNextMatches(sp -> sp.equals(speaker) || sp.equals(newSpeaker))
                .expectNextMatches(sp -> sp.equals(speaker) || sp.equals(newSpeaker))
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdateSpeakerWhenSpeakersHaveSameNameAndSameEmailButDiffTwitterUsernames() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker1 = updateSpeaker(speaker, false, true);
        speakerService.upsert(speaker).block();
        speakerService.upsert(updatedSpeaker1).block();

        StepVerifier.create(speakerService.findByName(speaker.getName()))
                .expectNext(updatedSpeaker1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdateSpeakerWhenSpeakersHaveSameNameAndSameTwitterUsernameButDiffEmailsSpeaker() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker1 = updateSpeaker(speaker, true, false);
        speakerService.upsert(speaker).block();
        speakerService.upsert(updatedSpeaker1).block();

        StepVerifier.create(speakerService.findByName(speaker.getName()))
                .expectNext(updatedSpeaker1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdateSpeakerWhenSpeakersHaveSameNameAndSameTwitterUsernameAndSameEmailSpeaker() {
        final var speaker = generateSpeaker();
        final var updatedSpeaker = updateSpeaker(speaker);
        speakerService.upsert(speaker).block();
        speakerService.upsert(updatedSpeaker).block();

        StepVerifier.create(speakerService.findByName(speaker.getName()))
                .expectNext(updatedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersByName() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = updateSpeaker(speaker1, true, true);
        speakerService.upsert(speaker1).block();
        speakerService.upsert(speaker2).block();

        StepVerifier.create(speakerService.findByName(speaker1.getName()))
                .expectNextMatches(sp -> sp.equals(speaker1) || sp.equals(speaker2))
                .expectNextMatches(sp -> sp.equals(speaker1) || sp.equals(speaker2))
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakerById() {
        final var speaker = generateSpeaker();
        speakerService.upsert(speaker).block();

        StepVerifier.create(speakerService.findBy(speaker.getId()))
                .expectNext(speaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersById() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        speakerService.upsert(speaker1).block();
        speakerService.upsert(speaker2).block();

        StepVerifier.create(speakerService.findBy(speaker1.getId()))
                .expectNext(speaker1)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(speaker2.getId()))
                .expectNext(speaker2)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakerByConferenceName() {
        final var speaker = generateSpeaker();
        final var conference = generateConference();

        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();

        final var expectedSpeaker = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker));

        StepVerifier.create(speakerService.findBy(conference.getName()))
                .expectNext(expectedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithSameNameAndSameConferenceYearByConferenceName() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        speaker2.setName(speaker1.getName());
        final var conference = generateConference();

        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker1, conference.getName(), conference.getYear()).block();
        speakerService.upsert(speaker2, conference.getName(), conference.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference.getName()))
                .expectNextMatches(speaker -> speaker.equals(expectedSpeaker1) || speaker.equals(expectedSpeaker2))
                .expectNextMatches(speaker -> speaker.equals(expectedSpeaker1) || speaker.equals(expectedSpeaker2))
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithSameNameAndDiffConferenceYearsByConferenceName() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        speaker2.setName(speaker1.getName());
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);

        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();
        speakerService.upsert(speaker1, conference1.getName(), conference1.getYear()).block();
        speakerService.upsert(speaker2, conference2.getName(), conference2.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference1.getName()))
                .expectNext(expectedSpeaker2)
                .expectNext(expectedSpeaker1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithDiffNamesAndSameConferenceYearByConferenceName() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        final var conference = generateConference();

        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker1, conference.getName(), conference.getYear()).block();
        speakerService.upsert(speaker2, conference.getName(), conference.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker2));

        boolean isSpeaker1First = speaker1.getName().compareTo(speaker2.getName()) < 0;

        StepVerifier.create(speakerService.findBy(conference.getName()))
                .expectNext(isSpeaker1First ? expectedSpeaker1 : expectedSpeaker2)
                .expectNext(isSpeaker1First ? expectedSpeaker2 : expectedSpeaker1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithDiffNamesAndDiffConferenceYearsByConferenceName() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);

        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();
        speakerService.upsert(speaker1, conference1.getName(), conference1.getYear()).block();
        speakerService.upsert(speaker2, conference2.getName(), conference2.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference1.getName()))
                .expectNext(expectedSpeaker2)
                .expectNext(expectedSpeaker1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakerByConferenceNameAndYear() {
        final var speaker = generateSpeaker();
        final var conference = generateConference();

        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker, conference.getName(), conference.getYear()).block();

        final var expectedSpeaker = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker));

        StepVerifier.create(speakerService.findBy(conference.getName(), conference.getYear()))
                .expectNext(expectedSpeaker)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithSameNameAndSameYearByConferenceNameAndYear() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        speaker2.setName(speaker1.getName());
        final var conference = generateConference();

        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker1, conference.getName(), conference.getYear()).block();
        speakerService.upsert(speaker2, conference.getName(), conference.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference.getName(), conference.getYear()))
                .expectNextMatches(speaker -> speaker.equals(expectedSpeaker1) || speaker.equals(expectedSpeaker2))
                .expectNextMatches(speaker -> speaker.equals(expectedSpeaker1) || speaker.equals(expectedSpeaker2))
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithSameNameAndDiffConferenceYearsByConferenceNameAndYear() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        speaker2.setName(speaker1.getName());
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);

        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();
        speakerService.upsert(speaker1, conference1.getName(), conference1.getYear()).block();
        speakerService.upsert(speaker2, conference2.getName(), conference2.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(expectedSpeaker1)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(conference1.getName(), conference2.getYear()))
                .expectNext(expectedSpeaker2)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithDiffNamesAndSameConferenceYearByConferenceNameAndYear() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        final var conference = generateConference();

        conferenceService.upsert(conference).block();
        speakerService.upsert(speaker1, conference.getName(), conference.getYear()).block();
        speakerService.upsert(speaker2, conference.getName(), conference.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference.getName(), conference.getYear(), speaker2));

        boolean isSpeaker1First = speaker1.getName().compareTo(speaker2.getName()) < 0;

        StepVerifier.create(speakerService.findBy(conference.getName(), conference.getYear()))
                .expectNext(isSpeaker1First ? expectedSpeaker1 : expectedSpeaker2)
                .expectNext(isSpeaker1First ? expectedSpeaker2 : expectedSpeaker1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindSpeakersWithDiffNamesAndDiffConferenceYearsByConferenceNameAndYear() {
        final var speaker1 = generateSpeaker();
        final var speaker2 = generateSpeaker();
        final var conference1 = generateConference();
        final var conference2 = Conference.from(conference1);
        conference2.setYear(conference1.getYear() + 1);

        conferenceService.upsert(conference1).block();
        conferenceService.upsert(conference2).block();
        speakerService.upsert(speaker1, conference1.getName(), conference1.getYear()).block();
        speakerService.upsert(speaker2, conference2.getName(), conference2.getYear()).block();

        final var expectedSpeaker1 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker1));
        final var expectedSpeaker2 = Speaker.from(
                SpeakerByConferenceEntity.from(conference1.getName(), conference1.getYear(), speaker2));

        StepVerifier.create(speakerService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(expectedSpeaker1)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(conference1.getName(), conference2.getYear()))
                .expectNext(expectedSpeaker2)
                .expectComplete()
                .verify();
    }
}

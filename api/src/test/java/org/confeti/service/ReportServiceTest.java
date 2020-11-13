package org.confeti.service;

import org.confeti.db.model.conference.ConferenceBySpeakerEntity;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.confeti.service.dto.Speaker;
import org.confeti.support.AbstractIntegrationTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static org.confeti.support.TestUtil.generateReport;

public class ReportServiceTest extends AbstractIntegrationTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private SpeakerService speakerService;

    @Test
    public void testInsertReport() {
        final var report = generateReport(1, 1, 1);

        final var expectedReport = Report.from(ReportEntity.from(report));
        StepVerifier.create(reportService.upsert(report))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertReportByConference() {
        final var report = generateReport(1, 1, 1);
        final var conference = report.getConferences().iterator().next();
        conferenceService.upsert(conference).block();

        final var expectedReport = Report.from(ReportEntity.from(report));
        StepVerifier.create(reportService.upsert(report, conference.getName(), conference.getYear()))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertReportWhenInsertReportByConference() {
        final var report = generateReport(1, 1, 1);
        final var conference = report.getConferences().iterator().next();
        conferenceService.upsert(conference).block();
        reportService.upsert(report, conference.getName(), conference.getYear()).block();

        final var expectedReport = Report.from(ReportEntity.from(report));
        StepVerifier.create(reportService.findBy(report.getId()))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertReportBySpeakerTest() {
        final var report = generateReport(1, 1, 1);
        final var conference = report.getConferences().iterator().next();
        final var speaker = report.getSpeakers().iterator().next();
        speakerService.upsert(speaker).block();

        final var expectedReport = Report.from(ReportEntity.from(report));
        StepVerifier.create(reportService.upsert(report, speaker.getId(), conference.getYear()))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertReportWhenInsertReportBySpeaker() {
        final var report = generateReport(1, 1, 1);
        final var conference = report.getConferences().iterator().next();
        final var speaker = report.getSpeakers().iterator().next();
        speakerService.upsert(speaker).block();
        reportService.upsert(report, speaker.getId(), conference.getYear()).block();

        final var expectedReport = Report.from(ReportEntity.from(report));
        StepVerifier.create(reportService.findBy(report.getId()))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertReportByTagTest() {
        final var report = generateReport(1, 1, 1);
        final var tag = report.getTags().iterator().next();

        final var expectedReport = Report.from(ReportEntity.from(report));
        StepVerifier.create(reportService.upsert(report, tag))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertReportWhenInsertReportByTagTest() {
        final var report = generateReport(1, 1, 1);
        final var tag = report.getTags().iterator().next();
        reportService.upsert(report, tag).block();

        final var expectedReport = Report.from(ReportEntity.from(report));
        StepVerifier.create(reportService.findBy(report.getId()))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    @Test
    public void testInsertConferencesWhenInsertReportTest() {
        testInsertConferencesWhen(this::insertReport);
    }

    @Test
    public void testInsertConferencesWhenInsertReportByConferenceTest() {
        testInsertConferencesWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertConferencesWhenInsertReportBySpeakerTest() {
        testInsertConferencesWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertConferencesWhenInsertReportByTagTest() {
        testInsertConferencesWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportTest() {
        testInsertReportByConferenceWhen(this::insertReport);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportByConferenceTest() {
        testInsertReportByConferenceWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportBySpeakerTest() {
        testInsertReportByConferenceWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportByTagTest() {
        testInsertReportByConferenceWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportTest() {
        testInsertSpeakersWhen(this::insertReport);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportByConferenceTest() {
        testInsertSpeakersWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportBySpeakerTest() {
        testInsertSpeakersWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportByTagTest() {
        testInsertSpeakersWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportTest() {
        testInsertReportBySpeakerWhen(this::insertReport);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportByConferenceTest() {
        testInsertReportBySpeakerWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportBySpeakerTest() {
        testInsertReportBySpeakerWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportByTagTest() {
        testInsertReportBySpeakerWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportTest() {
        testInsertSpeakerByConferenceWhen(this::insertReport);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportByConferenceTest() {
        testInsertSpeakerByConferenceWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportBySpeakerTest() {
        testInsertSpeakerByConferenceWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportByTagTest() {
        testInsertSpeakerByConferenceWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportTest() {
        testInsertConferenceBySpeakerWhen(this::insertReport);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportByConferenceTest() {
        testInsertConferenceBySpeakerWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportBySpeakerTest() {
        testInsertConferenceBySpeakerWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportByTagTest() {
        testInsertConferenceBySpeakerWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportTest() {
        testInsertReportByTagWhen(this::insertReport);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportByConferenceTest() {
        testInsertReportByTagWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportBySpeakerTest() {
        testInsertReportByTagWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportByTagTest() {
        testInsertReportByTagWhen(this::insertReportByTag);
    }

    private void insertReport(@NotNull final Report report) {
        reportService.upsert(report).block();
    }

    private void insertReportByConference(@NotNull final Report report) {
        final var conference = report.getConferences().iterator().next();
        reportService.upsert(report, conference.getName(), conference.getYear()).block();
    }

    private void insertReportBySpeaker(@NotNull final Report report) {
        final var speaker = report.getSpeakers().iterator().next();
        final var conference = report.getConferences().iterator().next();
        reportService.upsert(report, speaker.getId(), conference.getYear()).block();
    }

    private void insertReportByTag(@NotNull final Report report) {
        final var tag = report.getTags().iterator().next();
        reportService.upsert(report, tag).block();
    }

    private void testInsertConferencesWhen(@NotNull final Consumer<Report> upsertReport) {
        final var report = generateReport(2, 1, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        upsertReport.accept(report);

        StepVerifier.create(conferenceService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(conference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference2.getName(), conference2.getYear()))
                .expectNext(conference2)
                .expectComplete()
                .verify();
    }

    private void testInsertReportByConferenceWhen(@NotNull final Consumer<Report> upsertReport) {
        final var report = generateReport(2, 1, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        upsertReport.accept(report);

        final var expectedReport1 = Report.from(
                ReportByConferenceEntity.from(conference1.getName(), conference1.getYear(), report));
        StepVerifier.create(reportService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(expectedReport1)
                .expectComplete()
                .verify();

        final var expectedReport2 = Report.from(
                ReportByConferenceEntity.from(conference2.getName(), conference2.getYear(), report));
        StepVerifier.create(reportService.findBy(conference2.getName(), conference2.getYear()))
                .expectNext(expectedReport2)
                .expectComplete()
                .verify();
    }

    private void testInsertReportByTagWhen(@NotNull final Consumer<Report> upsertReport) {
        final var report = generateReport(1, 1, 2);
        final var tagIterator = report.getTags().iterator();
        final var tag1 = tagIterator.next();
        final var tag2 = tagIterator.next();
        upsertReport.accept(report);

        final var expectedReport1 = Report.from(ReportByTagEntity.from(tag1, report));
        StepVerifier.create(reportService.findBy(tag1, report.getTitle()))
                .expectNext(expectedReport1)
                .expectComplete()
                .verify();

        final var expectedReport2 = Report.from(ReportByTagEntity.from(tag2, report));
        StepVerifier.create(reportService.findBy(tag2, report.getTitle()))
                .expectNext(expectedReport2)
                .expectComplete()
                .verify();
    }

    private void testInsertSpeakersWhen(@NotNull final Consumer<Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var speakerIterator = report.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();
        upsertReport.accept(report);

        StepVerifier.create(speakerService.findBy(speaker1.getId()))
                .expectNext(speaker1)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(speaker2.getId()))
                .expectNext(speaker2)
                .expectComplete()
                .verify();
    }

    private void testInsertReportBySpeakerWhen(@NotNull final Consumer<Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var conference = report.getConferences().iterator().next();
        final var speakerIterator = report.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();
        upsertReport.accept(report);

        final var expectedReport1 = Report.from(
                ReportBySpeakerEntity.from(speaker1.getId(), conference.getYear(), report));
        StepVerifier.create(reportService.findBy(speaker1.getId(), conference.getYear(), report.getTitle()))
                .expectNext(expectedReport1)
                .expectComplete()
                .verify();

        final var expectedReport2 = Report.from(
                ReportBySpeakerEntity.from(speaker1.getId(), conference.getYear(), report));
        StepVerifier.create(reportService.findBy(speaker2.getId(), conference.getYear(), report.getTitle()))
                .expectNext(expectedReport2)
                .expectComplete()
                .verify();
    }

    private void testInsertSpeakerByConferenceWhen(@NotNull final Consumer<Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var conference = report.getConferences().iterator().next();
        final var speakerIterator = report.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();
        upsertReport.accept(report);

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

    private void testInsertConferenceBySpeakerWhen(@NotNull final Consumer<Report> upsertReport) {
        final var report = generateReport(2, 1, 1);
        final var speaker = report.getSpeakers().iterator().next();
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        upsertReport.accept(report);

        final var expectedConference1 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference1));
        final var expectedConference2 = Conference.from(
                ConferenceBySpeakerEntity.from(speaker.getId(), conference2));

        boolean isConference1First;

        if (conference1.getYear() > conference2.getYear()) {
            isConference1First = true;
        } else if (conference1.getYear() < conference2.getYear()) {
            isConference1First = false;
        } else {
            isConference1First = conference1.getName().compareTo(conference2.getName()) < 0;
        }

        StepVerifier.create(conferenceService.findBy(speaker.getId()))
                .expectNext(isConference1First ? expectedConference1 : expectedConference2)
                .expectNext(isConference1First ? expectedConference2 : expectedConference1)
                .expectComplete()
                .verify();
    }
}

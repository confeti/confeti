package org.confeti.service;

import org.confeti.db.model.conference.ConferenceBySpeakerEntity;
import org.confeti.db.model.report.ReportByCompanyEntity;
import org.confeti.db.model.report.ReportByConferenceEntity;
import org.confeti.db.model.report.ReportBySpeakerEntity;
import org.confeti.db.model.report.ReportByTagEntity;
import org.confeti.db.model.report.ReportEntity;
import org.confeti.db.model.speaker.SpeakerByConferenceEntity;
import org.confeti.service.dto.Company;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.confeti.service.dto.Speaker;
import org.confeti.service.dto.stats.ReportStatsByCompany;
import org.confeti.service.dto.stats.ReportStatsByConference;
import org.confeti.service.dto.stats.ReportStatsBySpeakerForConference;
import org.confeti.service.dto.stats.ReportStatsBySpeakerForYear;
import org.confeti.support.AbstractIntegrationTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

import static org.confeti.support.TestUtil.generateReport;
import static org.confeti.support.TestUtil.generateSpeaker;
import static org.confeti.support.TestUtil.updateReport;

public class ReportServiceTest extends AbstractIntegrationTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportStatsService reportStatsService;

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private CompanyService companyService;


    @Test
    public void testInsertReport() {
        testInsertReportWhen(this::insertReport);
    }

    @Test
    public void testInsertReportWhenInsertReportByConference() {
        testInsertReportWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportWhenInsertReportBySpeaker() {
        testInsertReportWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportWhenInsertReportByTag() {
        testInsertReportWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportWhenInsertReportByCompany() {
        testInsertReportWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertConferencesWhenInsertReport() {
        testInsertConferencesWhen(this::insertReport);
    }

    @Test
    public void testInsertConferencesWhenInsertReportByConference() {
        testInsertConferencesWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertConferencesWhenInsertReportBySpeaker() {
        testInsertConferencesWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertConferencesWhenInsertReportByTag() {
        testInsertConferencesWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertConferencesWhenInsertReportByCompany() {
        testInsertConferencesWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReport() {
        testInsertReportByConferenceWhen(this::insertReport);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportByConference() {
        testInsertReportByConferenceWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportBySpeaker() {
        testInsertReportByConferenceWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportByTag() {
        testInsertReportByConferenceWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportByConferenceWhenInsertReportByCompany() {
        testInsertReportByConferenceWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertSpeakersWhenInsertReport() {
        testInsertSpeakersWhen(this::insertReport);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportByConference() {
        testInsertSpeakersWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportBySpeaker() {
        testInsertSpeakersWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportByTag() {
        testInsertSpeakersWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertSpeakersWhenInsertReportByCompany() {
        testInsertSpeakersWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertCompaniesWhenInsertReport() {
        testInsertCompaniesWhen(this::insertReport);
    }

    @Test
    public void testInsertCompaniesWhenInsertReportByConference() {
        testInsertCompaniesWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertCompaniesWhenInsertReportBySpeaker() {
        testInsertCompaniesWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertCompaniesWhenInsertReportByTag() {
        testInsertCompaniesWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertCompaniesWhenInsertReportByCompany() {
        testInsertCompaniesWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReport() {
        testInsertReportBySpeakerWhen(this::insertReport);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportByConference() {
        testInsertReportBySpeakerWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportBySpeaker() {
        testInsertReportBySpeakerWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportByTag() {
        testInsertReportBySpeakerWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportBySpeakerWhenInsertReportByCompany() {
        testInsertReportBySpeakerWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReport() {
        testInsertSpeakerByConferenceWhen(this::insertReport);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportByConference() {
        testInsertSpeakerByConferenceWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportBySpeaker() {
        testInsertSpeakerByConferenceWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportByTag() {
        testInsertSpeakerByConferenceWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertSpeakerByConferenceWhenInsertReportByCompany() {
        testInsertSpeakerByConferenceWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReport() {
        testInsertConferenceBySpeakerWhen(this::insertReport);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportByConference() {
        testInsertConferenceBySpeakerWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportBySpeaker() {
        testInsertConferenceBySpeakerWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportByTag() {
        testInsertConferenceBySpeakerWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertConferenceBySpeakerWhenInsertReportByCompany() {
        testInsertConferenceBySpeakerWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertReportByTagWhenInsertReport() {
        testInsertReportByTagWhen(this::insertReport);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportByConference() {
        testInsertReportByTagWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportBySpeaker() {
        testInsertReportByTagWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportByTag() {
        testInsertReportByTagWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportByTagWhenInsertReportByCompany() {
        testInsertReportByTagWhen(this::insertReportByCompany);
    }

    @Test
    public void testInsertReportByCompanyWhenInsertReport() {
        testInsertReportByCompanyWhen(this::insertReport);
    }

    @Test
    public void testInsertReportByCompanyWhenInsertReportByConference() {
        testInsertReportByCompanyWhen(this::insertReportByConference);
    }

    @Test
    public void testInsertReportByCompanyWhenInsertReportBySpeaker() {
        testInsertReportByCompanyWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testInsertReportByCompanyWhenInsertReportByTag() {
        testInsertReportByCompanyWhen(this::insertReportByTag);
    }

    @Test
    public void testInsertReportByCompanyWhenInsertReportByCompany() {
        testInsertReportByCompanyWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhenInsertReport() {
        testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhenInsertReportByConference() {
        testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhenInsertReportBySpeaker() {
        testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhenInsertReportByTag() {
        testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhenInsertReportByCompany() {
        testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhen(this::insertReportByCompany);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhenInsertReport() {
        testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhen(this::insertReport);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhenInsertReportByConference() {
        testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhen(this::insertReportByConference);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhenInsertReportBySpeaker() {
        testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhenInsertReportByTag() {
        testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhen(this::insertReportByTag);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhenInsertReportByCompany() {
        testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhen(this::insertReportByCompany);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhenInsertReport() {
        testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhen(this::insertReport);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhenInsertReportByConference() {
        testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhen(this::insertReportByConference);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhenInsertReportBySpeaker() {
        testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhenInsertReportByTag() {
        testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhen(this::insertReportByTag);
    }

    @Test
    public void testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhenInsertReportByCompany() {
        testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhenInsertReport() {
        testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhenInsertReportByConference() {
        testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhenInsertReportBySpeaker() {
        testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhenInsertReportByTag() {
        testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhenInsertReportByCompany() {
        testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhenInsertReport() {
        testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhenInsertReportByConference() {
        testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhenInsertReportBySpeaker() {
        testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhenInsertReportByTag() {
        testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhenInsertReportByCompany() {
        testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhenInsertReport() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhenInsertReportByConference() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhenInsertReportBySpeaker() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhenInsertReportByTag() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhenInsertReportByCompany() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhenInsertReport() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhenInsertReportByConference() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhenInsertReportBySpeaker() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhenInsertReportByTag() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhenInsertReportByCompany() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhenInsertReport() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhenInsertReportByConference() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhenInsertReportBySpeaker() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhenInsertReportByTag() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhenInsertReportByCompany() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhenInsertReport() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhenInsertReportByConference() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhenInsertReportBySpeaker() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhenInsertReportByTag() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhenInsertReportByCompany() {
        testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhenInsertReport() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhenInsertReportByConference() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhenInsertReportBySpeaker() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhenInsertReportByTag() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhenInsertReportByCompany() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhen(this::insertReportByCompany);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhenInsertReport() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhen(this::insertReport);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhenInsertReportByConference() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhen(this::insertReportByConference);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhenInsertReportBySpeaker() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhen(this::insertReportBySpeaker);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhenInsertReportByTag() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhen(this::insertReportByTag);
    }

    @Test
    public void testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhenInsertReportByCompany() {
        testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhen(this::insertReportByCompany);
    }

    private Report insertReport(@NotNull final Report report) {
        return reportService.upsert(report).block();
    }

    private Report insertReportByConference(@NotNull final Report report) {
        final var conference = report.getConferences().iterator().next();
        return reportService.upsertByConference(report, conference.getName(), conference.getYear()).block();
    }

    private Report insertReportBySpeaker(@NotNull final Report report) {
        final var speaker = report.getSpeakers().iterator().next();
        speaker.setId(UUID.randomUUID());
        final var year = report.getConferences().iterator().next().getYear();
        return reportService.upsertBySpeaker(report, speaker.getId(), year).block();
    }

    private Report insertReportByTag(@NotNull final Report report) {
        final var tag = report.getTags().iterator().next();
        return reportService.upsertByTag(report, tag).block();
    }

    private Report insertReportByCompany(@NotNull final Report report) {
        final var year = report.getConferences().iterator().next().getYear();
        final var company = report.getSpeakers().iterator().next().getContactInfo().getCompany().getName();
        return reportService.upsertByCompany(report, company, year).block();
    }

    private void testInsertReportWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 1, 1);
        final var savedReport = upsertReport.apply(report);

        final var expectedReport = Report.from(ReportEntity.from(savedReport));

        StepVerifier.create(reportService.findById(savedReport.getId()))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportIfExistReportWithSameTitleAndSameSpeakersWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var updatedReport = updateReport(report, false, false);
        upsertReport.apply(report);
        upsertReport.apply(updatedReport);

        final var expectedReport = Report.from(ReportEntity.from(updatedReport));

        StepVerifier.create(reportService.findByTitle(report.getTitle()))
                .expectNext(expectedReport)
                .expectComplete()
                .verify();
    }

    private void testNotUpdateReportIfExistReportWithSameTitleAndSomeSpeakersCoincidedWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var newReport = updateReport(report, false, false);
        newReport.getSpeakers().add(generateSpeaker());
        upsertReport.apply(report);
        upsertReport.apply(newReport);

        final var expectedReport1 = Report.from(ReportEntity.from(report));
        final var expectedReport2 = Report.from(ReportEntity.from(newReport));

        StepVerifier.create(reportService.findByTitle(report.getTitle()))
                .expectNextMatches(rep -> rep.equals(expectedReport1) || rep.equals(expectedReport2))
                .expectNextMatches(rep -> rep.equals(expectedReport1) || rep.equals(expectedReport2))
                .expectComplete()
                .verify();
    }

    private void testNotUpdateReportIfExistReportWithSameTitleAndSpeakersNotCoincidedWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var newReport = updateReport(report, true, false);
        newReport.setSpeakers(Sets.newSet(generateSpeaker()));
        upsertReport.apply(report);
        upsertReport.apply(newReport);

        final var expectedReport1 = Report.from(ReportEntity.from(report));
        final var expectedReport2 = Report.from(ReportEntity.from(newReport));

        StepVerifier.create(reportService.findByTitle(report.getTitle()))
                .expectNextMatches(rep -> rep.equals(expectedReport1) || rep.equals(expectedReport2))
                .expectNextMatches(rep -> rep.equals(expectedReport1) || rep.equals(expectedReport2))
                .expectComplete()
                .verify();
    }

    private void testInsertConferencesWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(2, 1, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        upsertReport.apply(report);

        StepVerifier.create(conferenceService.findBy(conference1.getName(), conference1.getYear()))
                .expectNext(conference1)
                .expectComplete()
                .verify();

        StepVerifier.create(conferenceService.findBy(conference2.getName(), conference2.getYear()))
                .expectNext(conference2)
                .expectComplete()
                .verify();
    }

    private void testInsertReportByConferenceWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(2, 1, 1);
        final var savedReport = upsertReport.apply(report);
        final var conferenceIterator = savedReport.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();

        final var expectedReport1 = Report.from(
                ReportByConferenceEntity.from(conference1.getName(), conference1.getYear(), report));
        StepVerifier.create(reportService.findByConference(conference1.getName(), conference1.getYear()))
                .expectNext(expectedReport1)
                .expectComplete()
                .verify();

        final var expectedReport2 = Report.from(
                ReportByConferenceEntity.from(conference2.getName(), conference2.getYear(), report));
        StepVerifier.create(reportService.findByConference(conference2.getName(), conference2.getYear()))
                .expectNext(expectedReport2)
                .expectComplete()
                .verify();
    }

    private void testInsertReportByTagWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 1, 2);
        final var savedReport = upsertReport.apply(report);
        final var tagIterator = savedReport.getTags().iterator();
        final var tag1 = tagIterator.next();
        final var tag2 = tagIterator.next();

        final var expectedReport1 = Report.from(ReportByTagEntity.from(tag1, report));
        StepVerifier.create(reportService.findByTag(tag1, report.getTitle()))
                .expectNext(expectedReport1)
                .expectComplete()
                .verify();

        final var expectedReport2 = Report.from(ReportByTagEntity.from(tag2, report));
        StepVerifier.create(reportService.findByTag(tag2, report.getTitle()))
                .expectNext(expectedReport2)
                .expectComplete()
                .verify();
    }

    private void testInsertReportByCompanyWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var savedReport = upsertReport.apply(report);
        final var year = savedReport.getConferences().iterator().next().getYear();
        final var speakerIterator = savedReport.getSpeakers().iterator();
        final var company1 = speakerIterator.next().getContactInfo().getCompany().getName();
        final var company2 = speakerIterator.next().getContactInfo().getCompany().getName();

        final var expectedReport1 = Report.from(ReportByCompanyEntity.from(company1, year, report));
        StepVerifier.create(reportService.findByCompany(company1, year, report.getTitle()))
                .expectNext(expectedReport1)
                .expectComplete()
                .verify();

        final var expectedReport2 = Report.from(ReportByCompanyEntity.from(company2, year, report));
        StepVerifier.create(reportService.findByCompany(company2, year, report.getTitle()))
                .expectNext(expectedReport2)
                .expectComplete()
                .verify();
    }

    private void testInsertCompaniesWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var savedReport = upsertReport.apply(report);
        final var speakerIterator = savedReport.getSpeakers().iterator();
        final var company1 = speakerIterator.next().getContactInfo().getCompany().getName();
        final var company2 = speakerIterator.next().getContactInfo().getCompany().getName();

        final var expectedCompany1 = Company.builder(company1).build();
        final var expectedCompany2 = Company.builder(company2).build();

        StepVerifier.create(companyService.findBy(company1))
                .expectNext(expectedCompany1)
                .expectComplete()
                .verify();

        StepVerifier.create(companyService.findBy(company2))
                .expectNext(expectedCompany2)
                .expectComplete()
                .verify();
    }

    private void testInsertSpeakersWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var savedReport = upsertReport.apply(report);
        final var speakerIterator = savedReport.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();

        StepVerifier.create(speakerService.findBy(speaker1.getId()))
                .expectNext(speaker1)
                .expectComplete()
                .verify();

        StepVerifier.create(speakerService.findBy(speaker2.getId()))
                .expectNext(speaker2)
                .expectComplete()
                .verify();
    }

    private void testInsertReportBySpeakerWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var savedReport = upsertReport.apply(report);
        final var conference = savedReport.getConferences().iterator().next();
        final var speakerIterator = savedReport.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();

        final var expectedReport1 = Report.from(
                ReportBySpeakerEntity.from(speaker1.getId(), conference.getYear(), report));
        StepVerifier.create(reportService.findBySpeaker(speaker1.getId(), conference.getYear(), report.getTitle()))
                .expectNext(expectedReport1)
                .expectComplete()
                .verify();

        final var expectedReport2 = Report.from(
                ReportBySpeakerEntity.from(speaker1.getId(), conference.getYear(), report));
        StepVerifier.create(reportService.findBySpeaker(speaker2.getId(), conference.getYear(), report.getTitle()))
                .expectNext(expectedReport2)
                .expectComplete()
                .verify();
    }

    private void testInsertSpeakerByConferenceWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(1, 2, 1);
        final var conference = report.getConferences().iterator().next();
        final var savedReport = upsertReport.apply(report);
        final var speakerIterator = savedReport.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();

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

    private void testInsertConferenceBySpeakerWhen(@NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(2, 1, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        final var savedReport = upsertReport.apply(report);
        final var speaker = savedReport.getSpeakers().iterator().next();

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

    private void testUpdateReportConferenceStatsWith2ReportsAnd3ConfsWhere2ConfsHaveSameNameButDiffYearsWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report1 = generateReport(2, 1, 1);
        final var report2 = generateReport(2, 1, 1);
        final var conferenceIterator = report1.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        final var newConference1 = Conference.from(conference1);
        newConference1.setYear(conference1.getYear() + 1);
        report2.setConferences(Sets.newSet(newConference1, Conference.from(conference2)));
        upsertReport.apply(report1);
        upsertReport.apply(report2);

        final var expectedReportStatsByConf1 = ReportStatsByConference.builder()
                .conferenceName(conference1.getName())
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsByConf2 = ReportStatsByConference.builder()
                .conferenceName(conference1.getName())
                .year(newConference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsByConf3 = ReportStatsByConference.builder()
                .conferenceName(conference2.getName())
                .year(conference2.getYear())
                .reportTotal(2L)
                .build();

        StepVerifier.create(reportStatsService.countConferenceStats(conference1.getName()))
                .expectNext(expectedReportStatsByConf2)
                .expectNext(expectedReportStatsByConf1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countConferenceStatsForYear(conference2.getName(), conference2.getYear()))
                .expectNext(expectedReportStatsByConf3)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportSpeakerStatsWith2SpeakersWhereFirstWas2DiffConfsInOneYearAndSecondWasSameConfInDiffYearsWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report1 = generateReport(2, 1, 1);
        final var report2 = generateReport(2, 1, 1);
        final var conferenceIterator = report1.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        conference2.setYear(conference1.getYear());
        final var newConference1 = Conference.from(conference1);
        newConference1.setYear(conference1.getYear() + 1);
        report2.setConferences(Sets.newSet(Conference.from(conference1), newConference1));

        final var savedReport1 = upsertReport.apply(report1);
        final var savedReport2 = upsertReport.apply(report2);
        final var speaker1 = savedReport1.getSpeakers().iterator().next();
        final var speaker2 = savedReport2.getSpeakers().iterator().next();

        final var expectedReportStatsBySpeaker1ForYear1 = ReportStatsBySpeakerForYear.builder()
                .speakerId(speaker1.getId())
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsBySpeaker2ForYear1 = ReportStatsBySpeakerForYear.builder()
                .speakerId(speaker2.getId())
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsBySpeaker2ForYear2 = ReportStatsBySpeakerForYear.builder()
                .speakerId(speaker2.getId())
                .year(newConference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsBySpeaker1ForConf1 = ReportStatsBySpeakerForConference.builder()
                .speakerId(speaker1.getId())
                .conferenceName(conference1.getName())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsBySpeaker2ForConf1 = ReportStatsBySpeakerForConference.builder()
                .speakerId(speaker2.getId())
                .conferenceName(conference1.getName())
                .reportTotal(2L)
                .build();
        final var expectedReportStatsBySpeaker1ForConf2 = ReportStatsBySpeakerForConference.builder()
                .speakerId(speaker1.getId())
                .conferenceName(conference2.getName())
                .reportTotal(1L)
                .build();

        StepVerifier.create(reportStatsService.countSpeakerStatsForYear(speaker1.getId(), conference1.getYear()))
                .expectNext(expectedReportStatsBySpeaker1ForYear1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countSpeakerStatsForConference(speaker1.getId(), conference1.getName()))
                .expectNext(expectedReportStatsBySpeaker1ForConf1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countSpeakerStatsForConference(speaker1.getId(), conference2.getName()))
                .expectNext(expectedReportStatsBySpeaker1ForConf2)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countSpeakerStatsForConference(speaker2.getId(), conference1.getName()))
                .expectNext(expectedReportStatsBySpeaker2ForConf1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countSpeakerStatsForYear(speaker2.getId(), conference1.getYear()))
                .expectNext(expectedReportStatsBySpeaker2ForYear1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countSpeakerStatsForYear(speaker2.getId(), newConference1.getYear()))
                .expectNext(expectedReportStatsBySpeaker2ForYear2)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromSameCompanyWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(2, 2, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        conference2.setYear(conference1.getYear() + 1);
        final var speakerIterator = report.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();
        final var companyName = RandomStringUtils.randomAlphabetic(5);
        speaker1.getContactInfo().getCompany().setName(companyName);
        speaker2.getContactInfo().getCompany().setName(companyName);

        upsertReport.apply(report);

        final var expectedReportStatsByCompany1 = ReportStatsByCompany.builder()
                .companyName(companyName)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsByCompany2 = ReportStatsByCompany.builder()
                .companyName(companyName)
                .year(conference2.getYear())
                .reportTotal(1L)
                .build();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(companyName, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(companyName, conference2.getYear()))
                .expectNext(expectedReportStatsByCompany2)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromSameCompanyWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(2, 2, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        conference2.setYear(conference1.getYear());
        final var speakerIterator = report.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();
        final var companyName = RandomStringUtils.randomAlphabetic(5);
        speaker1.getContactInfo().getCompany().setName(companyName);
        speaker2.getContactInfo().getCompany().setName(companyName);

        upsertReport.apply(report);

        final var expectedReportStatsByCompany = ReportStatsByCompany.builder()
                .companyName(companyName)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(companyName, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesSameYearAnd2SpeakersFromDiffCompaniesWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(2, 2, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        conference2.setYear(conference1.getYear());
        final var speakerIterator = report.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();
        final var company1 = speaker1.getContactInfo().getCompany().getName();
        final var company2 = speaker2.getContactInfo().getCompany().getName();

        upsertReport.apply(report);

        final var expectedReportStatsByCompany1 = ReportStatsByCompany.builder()
                .companyName(company1)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsByCompany2 = ReportStatsByCompany.builder()
                .companyName(company2)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company1, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company2, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany2)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportCompanyStatsWith1ReportAnd2ConferencesDiffYearsAnd2SpeakersFromDiffCompaniesWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report = generateReport(2, 2, 1);
        final var conferenceIterator = report.getConferences().iterator();
        final var conference1 = conferenceIterator.next();
        final var conference2 = conferenceIterator.next();
        conference2.setYear(conference1.getYear() + 1);
        final var speakerIterator = report.getSpeakers().iterator();
        final var speaker1 = speakerIterator.next();
        final var speaker2 = speakerIterator.next();
        final var company1 = speaker1.getContactInfo().getCompany().getName();
        final var company2 = speaker2.getContactInfo().getCompany().getName();

        upsertReport.apply(report);

        final var expectedReportStatsByCompany1 = ReportStatsByCompany.builder()
                .companyName(company1)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsByCompany2 = ReportStatsByCompany.builder()
                .companyName(company2)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company1, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company2, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany2)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportCompanyStatsWith2ReportFromSameConferenceDiffYearsAnd1CompanyWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report1 = generateReport(1, 1, 1);
        final var report2 = generateReport(1, 1, 1);
        final var conference1 = report1.getConferences().iterator().next();
        final var conference2 = report2.getConferences().iterator().next();
        conference2.setYear(conference1.getYear() + 1);
        final var speaker1 = report1.getSpeakers().iterator().next();
        final var speaker2 = report2.getSpeakers().iterator().next();
        final var company = speaker1.getContactInfo().getCompany().getName();
        speaker2.getContactInfo().getCompany().setName(company);

        upsertReport.apply(report1);
        upsertReport.apply(report2);

        final var expectedReportStatsByCompany1 = ReportStatsByCompany.builder()
                .companyName(company)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsByCompany2 = ReportStatsByCompany.builder()
                .companyName(company)
                .year(conference2.getYear())
                .reportTotal(1L)
                .build();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company, conference2.getYear()))
                .expectNext(expectedReportStatsByCompany2)
                .expectComplete()
                .verify();
    }

    private void testUpdateReportCompanyStatsWith2ReportFromSameConferenceSameYearAnd1CompanyWhen(
            @NotNull final Function<Report, Report> upsertReport) {
        final var report1 = generateReport(1, 1, 1);
        final var report2 = generateReport(1, 1, 1);
        final var conference1 = report1.getConferences().iterator().next();
        final var conference2 = report2.getConferences().iterator().next();
        conference2.setYear(conference1.getYear());
        final var speaker1 = report1.getSpeakers().iterator().next();
        final var speaker2 = report2.getSpeakers().iterator().next();
        final var company = speaker1.getContactInfo().getCompany().getName();
        speaker2.getContactInfo().getCompany().setName(company);

        upsertReport.apply(report1);
        upsertReport.apply(report2);

        final var expectedReportStatsByCompany = ReportStatsByCompany.builder()
                .companyName(company)
                .year(conference1.getYear())
                .reportTotal(2L)
                .build();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany)
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdateReportCompanyStatsWith1ReportFromDiffConferenceSameYearAnd2CompanyAnd1SpeakerWhen() {
        final var report1 = generateReport(1, 1, 1);
        final var report2 = updateReport(report1, true, false);
        final var conference1 = report1.getConferences().iterator().next();
        final var conference2 = report2.getConferences().iterator().next();
        conference2.setName(conference1.getName() + RandomStringUtils.randomAlphabetic(5));
        conference2.setYear(conference1.getYear());
        final var speaker = report1.getSpeakers().iterator().next();
        final var speakerWithNewCompany = Speaker.from(speaker);
        final var company1 = speaker.getContactInfo().getCompany().getName();
        final var company2 = company1 + RandomStringUtils.randomAlphabetic(5);
        speakerWithNewCompany.getContactInfo().getCompany().setName(company2);
        speakerWithNewCompany.getContactInfo().getCompany().setAddedDate(speaker.getContactInfo().getCompany().getAddedDate().plus(Duration.ofDays(30)));
        report2.setSpeakers(Sets.newSet(speakerWithNewCompany));

        reportService.upsert(report1).block();
        reportService.upsert(report2).block();

        final var expectedReportStatsByCompany1 = ReportStatsByCompany.builder()
                .companyName(company1)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();
        final var expectedReportStatsByCompany2 = ReportStatsByCompany.builder()
                .companyName(company2)
                .year(conference1.getYear())
                .reportTotal(1L)
                .build();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company1, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany1)
                .expectComplete()
                .verify();

        StepVerifier.create(reportStatsService.countCompanyStatsForYear(company2, conference1.getYear()))
                .expectNext(expectedReportStatsByCompany2)
                .expectComplete()
                .verify();
    }
}

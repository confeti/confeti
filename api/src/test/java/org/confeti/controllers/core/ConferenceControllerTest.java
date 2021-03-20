package org.confeti.controllers.core;

import org.confeti.config.SecurityConfig;
import org.confeti.controllers.dto.Status;
import org.confeti.controllers.dto.core.ConferenceStatResponse;
import org.confeti.controllers.dto.core.InputData;
import org.confeti.service.ConferenceService;
import org.confeti.service.ReportService;
import org.confeti.service.ReportStatsService;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.confeti.service.dto.stats.ReportStatsByConference;
import org.confeti.support.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.google.common.collect.Sets;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.confeti.controllers.core.StatisticControllerTestUtils.testGetListResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ConferenceController.class, ReportStatsService.class, ConferenceService.class, SecurityConfig.class})
@RunWith(SpringRunner.class)
public class ConferenceControllerTest {
    @MockBean
    private ReportStatsService reportStatsService;

    @MockBean
    private ConferenceService conferenceService;

    @MockBean
    private ReportService reportService;

    @Autowired
    private ConferenceController conferenceController;

    @Test
    public void testGetConferenceStatRequestWithYear() {
        final String conferenceName = "testConference";
        final int year = 1972;
        final long amount = 5L;

        final ConferenceStatResponse expectedResponse = new ConferenceStatResponse()
                .setConferenceName(conferenceName)
                .setYears(Map.of(year, amount));

        when(reportStatsService.countConferenceStatsForYear(conferenceName, 1972))
                .thenReturn(Mono.just(
                        ReportStatsByConference.builder()
                                .conferenceName(conferenceName)
                                .year(year)
                                .reportTotal(amount).build()));

        WebTestClient
                .bindToController(conferenceController)
                .build()
                .get()
                .uri(String.format("/api/rest/conference/%s/stat?year=%d", conferenceName, year))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ConferenceStatResponse.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetConferenceStatRequestWithoutYearForOneYear() {
        final String conferenceName = "test-conference";
        final int year = 1972;
        final long amount = 5L;
        final ConferenceStatResponse expectedResponse = new ConferenceStatResponse()
                .setConferenceName(conferenceName)
                .setYears(Map.of(year, amount));

        when(reportStatsService.countConferenceStats(conferenceName))
                .thenReturn(Flux.fromIterable(Collections.singletonList(
                        ReportStatsByConference.builder()
                                .conferenceName(conferenceName)
                                .year(year)
                                .reportTotal(amount)
                                .build())));

        WebTestClient
                .bindToController(conferenceController)
                .build()
                .get()
                .uri(String.format("/api/rest/conference/%s/stat", conferenceName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ConferenceStatResponse.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetConferenceStatRequestWithoutYearForMultipleYears() {
        final String conferenceName = "test-conference";
        final int year1 = 1972;
        final long amount1 = 5L;

        final int year2 = 1973;
        final long amount2 = 15L;

        final ConferenceStatResponse expectedResponse = new ConferenceStatResponse()
                .setConferenceName(conferenceName)
                .setYears(Map.of(year1, amount1, year2, amount2));

        when(reportStatsService.countConferenceStats(conferenceName))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        ReportStatsByConference.builder()
                                .conferenceName(conferenceName)
                                .year(year1)
                                .reportTotal(amount1)
                                .build(),
                        ReportStatsByConference.builder()
                                .conferenceName(conferenceName)
                                .year(year2)
                                .reportTotal(amount2)
                                .build()
                                              )
                ));

        WebTestClient
                .bindToController(conferenceController)
                .build()
                .get()
                .uri(String.format("/api/rest/conference/%s/stat", conferenceName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ConferenceStatResponse.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetAllConferencesSuccess() {
        final List<Conference> conferences = Arrays.asList(
                Conference.builder("company 1", 1917).build(),
                Conference.builder("company 2", 1917).build(),
                Conference.builder("company 1", 1920).build(),
                Conference.builder("company 3", 1925).build()
        );

        when(conferenceService.findAll()).thenReturn(Flux.fromIterable(conferences));

        testGetListResponse(conferenceController, "/api/rest/conference", conferences, Conference[].class);
        verify(conferenceService).findAll();
    }

    @Test
    public void testGetAllConferencesStatSuccess() {
        final String conference1 = "conference 1";
        final String conference2 = "conference 2";

        when(reportStatsService.countConferenceStats()).thenReturn(Flux.fromIterable(Arrays.asList(
                ReportStatsByConference.builder()
                        .year(1972)
                        .conferenceName(conference1)
                        .reportTotal(2L)
                        .build(),
                ReportStatsByConference.builder()
                        .year(1971)
                        .conferenceName(conference1)
                        .reportTotal(5L)
                        .build(),
                ReportStatsByConference.builder()
                        .conferenceName(conference2)
                        .year(1973)
                        .reportTotal(4L)
                        .build(),
                ReportStatsByConference.builder()
                        .conferenceName(conference2)
                        .year(1972)
                        .reportTotal(1L)
                        .build(),
                ReportStatsByConference.builder()
                        .conferenceName(conference2)
                        .year(1975)
                        .reportTotal(9L)
                        .build()
        )));

        List<ConferenceStatResponse> statResponses = Arrays.asList(
                new ConferenceStatResponse()
                        .setConferenceName(conference1)
                        .setYears(Map.of(1971, 5L, 1972, 2L)),
                new ConferenceStatResponse()
                        .setConferenceName(conference2)
                        .setYears(Map.of(1973, 4L, 1972, 1L, 1975, 9L))
        );

        testGetListResponse(conferenceController, "/api/rest/conference/stat", statResponses,
                            ConferenceStatResponse[].class);
    }

    @Test
    public void testPostRouterFailResponseOnEmptyBody() {
        when(reportService.upsert(any())).thenReturn(Mono.just(Report.builder("").build()));
        when(conferenceService.upsert(any())).thenReturn(Mono.just(Conference.builder("aaa", 1970).build()));

        WebTestClient
                .bindToController(conferenceController)
                .build()
                .post()
                .uri("/api/rest/conference")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new InputData())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testPostRouterOkResponseOnConferenceBody() {
        Conference conference = Conference.builder("aaa", 1970).build();
        when(reportService.upsert(any())).thenReturn(Mono.just(Report.builder("").build()));
        when(conferenceService.upsert(any())).thenReturn(Mono.just(conference));

        WebTestClient
                .bindToController(conferenceController)
                .build()
                .post()
                .uri("/api/rest/conference")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new InputData().setConference(conference))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Status.class).isEqualTo(Status.SUCCESS);
        verify(conferenceService).upsert(any());
        verify(reportService, never()).upsert(any());
    }

    @Test
    public void testPostRouterOkResponseOnConferenceAndReportsBody() {
        Conference conference = Conference.builder("aaa", 1970).build();
        Report report = TestUtil.generateReport(1, 1, 1);
        report.setConferences(Sets.newHashSet(conference));
        when(reportService.upsert(any())).thenReturn(Mono.just(report));
        when(conferenceService.upsert(any())).thenReturn(Mono.just(conference));

        WebTestClient
                .bindToController(conferenceController)
                .build()
                .post()
                .uri("/api/rest/conference")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new InputData().setConference(conference).setReports(Collections.singletonList(report)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Status.class).isEqualTo(Status.SUCCESS);
        verify(reportService).upsert(any());
    }

    @Test
    public void testGetConferencesBySpeakerId() {
        final List<Conference> conferences = List.of(
                Conference.builder("company 1", 1917).build(),
                Conference.builder("company 2", 1917).build());
        final UUID speakerId = UUID.randomUUID();

        when(conferenceService.findBy(speakerId)).thenReturn(Flux.fromIterable(conferences));
        testGetListResponse(conferenceController, String.format("/api/rest/conference?speaker_id=%s", speakerId),
                            conferences,
                            Conference[].class);
        verify(conferenceService).findBy(speakerId);
    }

    @Test
    public void testGetConferencesBySpeakerIdAndYear() {
        final List<Conference> conferences = List.of(
                Conference.builder("company 1", 1917).build(),
                Conference.builder("company 2", 1917).build());
        final UUID speakerId = UUID.randomUUID();
        final int year = 1917;

        when(conferenceService.findBy(speakerId, year)).thenReturn(Flux.fromIterable(conferences));
        testGetListResponse(conferenceController,
                            String.format("/api/rest/conference?speaker_id=%s&year=%d", speakerId, year),
                            conferences,
                            Conference[].class);
        verify(conferenceService).findBy(speakerId, year);
    }

    @Test
    public void testGetConferencesByConferenceName() {
        final String conferenceName = "conf";
        final List<Conference> conferences = List.of(
                Conference.builder(conferenceName, 1917).build(),
                Conference.builder(conferenceName, 1917).build());


        when(conferenceService.findBy(conferenceName)).thenReturn(Flux.fromIterable(conferences));
        testGetListResponse(conferenceController,
                            String.format("/api/rest/conference/%s", conferenceName),
                            conferences,
                            Conference[].class);
        verify(conferenceService).findBy(conferenceName);
    }
}

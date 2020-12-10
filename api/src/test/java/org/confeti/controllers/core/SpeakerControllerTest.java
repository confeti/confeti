package org.confeti.controllers.core;

import org.confeti.config.SecurityConfig;
import org.confeti.controllers.dto.core.SpeakerStatResponseByConference;
import org.confeti.controllers.dto.core.SpeakerStatResponseByYear;
import org.confeti.service.ReportStatsService;
import org.confeti.service.SpeakerService;
import org.confeti.service.dto.Speaker;
import org.confeti.service.dto.stats.ReportStatsBySpeakerForConference;
import org.confeti.service.dto.stats.ReportStatsBySpeakerForYear;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.confeti.controllers.core.StatisticControllerTestUtils.testGetListResponse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {SpeakerController.class, ReportStatsService.class, SpeakerService.class, SecurityConfig.class})
@RunWith(SpringRunner.class)
public class SpeakerControllerTest {

    @MockBean
    private ReportStatsService reportStatsService;

    @MockBean
    private SpeakerService speakerService;

    @Autowired
    private SpeakerController speakerController;

    @Test
    public void testGetAllSpeakersSuccess() {
        final List<Speaker> speakers = Arrays.asList(
                new Speaker().setName("speaker 1"),
                new Speaker().setName("speaker 2")
        );

        when(speakerService.findAll()).thenReturn(Flux.fromIterable(speakers));

        testGetListResponse(speakerController, "/api/rest/speaker", speakers, Speaker[].class);
        verify(speakerService).findAll();
    }

    @Test
    public void testGetSpeakerRequestWithYear() {
        final UUID speakerId = UUID.randomUUID();
        final int year = 1972;
        final long amount = 5L;

        final SpeakerStatResponseByYear expectedResponse = new SpeakerStatResponseByYear()
                .setId(speakerId)
                .setYears(Map.of(year, amount));

        when(reportStatsService.countSpeakerStatsForYear(speakerId, year))
                .thenReturn(Mono.just(
                        ReportStatsBySpeakerForYear.builder()
                                .speakerId(speakerId)
                                .year(year)
                                .reportTotal(amount)
                                .build()));

        WebTestClient
                .bindToController(speakerController)
                .build()
                .get()
                .uri(String.format("/api/rest/speaker/%s/stat?year=%d", speakerId.toString(), year))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpeakerStatResponseByYear.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetSpeakerRequestWithConference() {
        final UUID speakerId = UUID.randomUUID();
        final String conferenceName = "test-conference";
        final long amount = 5L;

        final SpeakerStatResponseByConference expectedResponse = new SpeakerStatResponseByConference()
                .setId(speakerId)
                .setConferences(Map.of(conferenceName, amount));

        when(reportStatsService.countSpeakerStatsForConference(speakerId, conferenceName))
                .thenReturn(Mono.just(
                        ReportStatsBySpeakerForConference.builder()
                                .speakerId(speakerId)
                                .conferenceName(conferenceName)
                                .reportTotal(amount)
                                .build()));

        WebTestClient
                .bindToController(speakerController)
                .build()
                .get()
                .uri(String.format("/api/rest/speaker/%s/stat?conference_name=%s", speakerId.toString(), conferenceName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpeakerStatResponseByConference.class).isEqualTo(expectedResponse);
    }


    @Test
    public void testGetSpeakerStatRequestForAllYearsSuccess() {
        final UUID speakerId = UUID.randomUUID();
        final int year1 = 1972;
        final long amount1 = 5L;

        final int year2 = 1973;
        final long amount2 = 15L;

        final SpeakerStatResponseByYear expectedResponse = new SpeakerStatResponseByYear()
                .setId(speakerId)
                .setYears(Map.of(year1, amount1, year2, amount2));

        when(reportStatsService.countSpeakerStatsForYear(speakerId))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        ReportStatsBySpeakerForYear.builder()
                                .speakerId(speakerId)
                                .year(year1)
                                .reportTotal(amount1)
                                .build(),
                        ReportStatsBySpeakerForYear.builder()
                                .speakerId(speakerId)
                                .year(year2)
                                .reportTotal(amount2)
                                .build()
                        )
                ));

        WebTestClient
                .bindToController(speakerController)
                .build()
                .get()
                .uri(String.format("/api/rest/speaker/%s/stat/year", speakerId.toString()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpeakerStatResponseByYear.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetSpeakerStatRequestForAllConferencesSuccess() {
        final UUID speakerId = UUID.randomUUID();
        final String conference1 = "testConference-1";
        final long amount1 = 5L;

        final String conference2 = "testConference-2";
        final long amount2 = 15L;

        final SpeakerStatResponseByConference expectedResponse = new SpeakerStatResponseByConference()
                .setId(speakerId)
                .setConferences(Map.of(conference1, amount1, conference2, amount2));

        when(reportStatsService.countSpeakerStatsForConference(speakerId))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        ReportStatsBySpeakerForConference.builder()
                                .speakerId(speakerId)
                                .conferenceName(conference1)
                                .reportTotal(amount1)
                                .build(),
                        ReportStatsBySpeakerForConference.builder()
                                .speakerId(speakerId)
                                .conferenceName(conference2)
                                .reportTotal(amount2)
                                .build()
                        )
                ));

        WebTestClient
                .bindToController(speakerController)
                .build()
                .get()
                .uri(String.format("/api/rest/speaker/%s/stat/conference", speakerId.toString()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpeakerStatResponseByConference.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetAllSpeakersStatByYearSuccess() {
        final UUID speaker1Id = UUID.randomUUID();
        final UUID speaker2Id = UUID.randomUUID();


        when(reportStatsService.countSpeakerStatsForYear()).thenReturn(Flux.fromIterable(Arrays.asList(
                ReportStatsBySpeakerForYear.builder()
                        .year(1972)
                        .speakerId(speaker1Id)
                        .reportTotal(2L)
                        .build(),
                ReportStatsBySpeakerForYear.builder()
                        .year(1971)
                        .speakerId(speaker1Id)
                        .reportTotal(5L)
                        .build(),
                ReportStatsBySpeakerForYear.builder()
                        .speakerId(speaker2Id)
                        .year(1973)
                        .reportTotal(4L)
                        .build(),
                ReportStatsBySpeakerForYear.builder()
                        .speakerId(speaker2Id)
                        .year(1972)
                        .reportTotal(1L)
                        .build(),
                ReportStatsBySpeakerForYear.builder()
                        .speakerId(speaker2Id)
                        .year(1975)
                        .reportTotal(9L)
                        .build()
        )));

        List<SpeakerStatResponseByYear> statResponses = Arrays.asList(
                new SpeakerStatResponseByYear()
                        .setId(speaker1Id)
                        .setYears(Map.of(1971, 5L, 1972, 2L)),
                new SpeakerStatResponseByYear()
                        .setId(speaker2Id)
                        .setYears(Map.of(1973, 4L, 1972, 1L, 1975, 9L))
        );

        testGetListResponse(speakerController, "/api/rest/speaker/stat/year", statResponses, SpeakerStatResponseByYear[].class);
    }

    @Test
    public void testGetAllSpeakersStatByConferenceSuccess() {
        final UUID speaker1Id = UUID.randomUUID();
        final UUID speaker2Id = UUID.randomUUID();


        when(reportStatsService.countSpeakerStatsForConference()).thenReturn(Flux.fromIterable(Arrays.asList(
                ReportStatsBySpeakerForConference.builder()
                        .conferenceName("conference1")
                        .speakerId(speaker1Id)
                        .reportTotal(2L)
                        .build(),
                ReportStatsBySpeakerForConference.builder()
                        .conferenceName("1971")
                        .speakerId(speaker1Id)
                        .reportTotal(5L)
                        .build(),
                ReportStatsBySpeakerForConference.builder()
                        .speakerId(speaker2Id)
                        .conferenceName("test-1973")
                        .reportTotal(4L)
                        .build(),
                ReportStatsBySpeakerForConference.builder()
                        .speakerId(speaker2Id)
                        .conferenceName("conference1")
                        .reportTotal(1L)
                        .build(),
                ReportStatsBySpeakerForConference.builder()
                        .speakerId(speaker2Id)
                        .conferenceName("conference2")
                        .reportTotal(9L)
                        .build()
        )));

        List<SpeakerStatResponseByConference> statResponses = Arrays.asList(
                new SpeakerStatResponseByConference()
                        .setId(speaker1Id)
                        .setConferences(Map.of("1971", 5L, "conference1", 2L)),
                new SpeakerStatResponseByConference()
                        .setId(speaker2Id)
                        .setConferences(Map.of("test-1973", 4L, "conference1", 1L, "conference2", 9L))
        );
        testGetListResponse(speakerController, "/api/rest/speaker/stat/conference", statResponses, SpeakerStatResponseByConference[].class);
    }
}

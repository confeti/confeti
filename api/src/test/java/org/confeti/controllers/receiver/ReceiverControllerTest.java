package org.confeti.controllers.receiver;

import org.confeti.config.SecurityConfig;
import org.confeti.controllers.dto.Status;
import org.confeti.controllers.dto.receiver.InputData;
import org.confeti.service.ConferenceService;
import org.confeti.service.ReportService;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReceiverController.class, ReportService.class, ConferenceService.class, InputData.class, SecurityConfig.class})
@RunWith(SpringRunner.class)
public class ReceiverControllerTest {

    @MockBean
    private ReportService reportService;
    @MockBean
    private ConferenceService conferenceService;

    @Autowired
    private ReceiverController receiverController;

    @Test
    public void testPostRouterFailResponseOnEmptyBody() {
        when(reportService.upsert(any())).thenReturn(Mono.just(Report.builder("").build()));
        when(conferenceService.upsert(any())).thenReturn(Mono.just(Conference.builder("aaa", 1970).build()));

        WebTestClient
                .bindToController(receiverController)
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
                .bindToController(receiverController)
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
        Report report = Report.builder("").build();
        when(reportService.upsert(any())).thenReturn(Mono.just(report));
        when(conferenceService.upsert(any())).thenReturn(Mono.just(conference));

        WebTestClient
                .bindToController(receiverController)
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

}

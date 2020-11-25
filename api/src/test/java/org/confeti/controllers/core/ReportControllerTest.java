package org.confeti.controllers.core;

import org.confeti.config.SecurityConfig;
import org.confeti.controllers.dto.core.TagResponse;
import org.confeti.service.ReportService;
import org.confeti.service.dto.Conference;
import org.confeti.service.dto.Report;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReportController.class, ReportService.class, SecurityConfig.class})
@RunWith(SpringRunner.class)
public class ReportControllerTest {
    @MockBean
    private ReportService reportService;

    @Autowired
    private ReportController reportController;

    @Test
    public void testGetReportTagRequestWithYearAndConferenceName() {
        final String conferenceName = "bla";
        final int year = 1970;
        final TagResponse response = new TagResponse(conferenceName, Map.of(year, Map.of("a", 2L, "b", 1L, "c", 1L)));

        when(reportService.findBy(conferenceName, year))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .tags(Set.of("a", "b"))
                                .build(),
                        Report.builder("title2")
                                .tags(Set.of("a", "c"))
                                .build()
                )));

        WebTestClient
                .bindToController(reportController)
                .build()
                .get()
                .uri(String.format("/api/rest/report/tag?year=%d&conference_name=%s", year, conferenceName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TagResponse.class).isEqualTo(response);
    }

    @Test
    public void testGetReportTagRequestWithConferenceName() {
        final Conference conference1 = Conference.builder("test", 1971).build();
        final Conference conference2 = Conference.builder("test", 1972).build();

        final TagResponse response = new TagResponse(conference1.getName(), Map.of(1971, Map.of("a", 2L, "b", 1L, "c", 1L),
                1972, Map.of("a", 1L, "c", 1L)));

        when(reportService.findBy(conference1.getName()))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .tags(Set.of("a", "b"))
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title11")
                                .tags(Set.of("a", "c"))
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title2")
                                .tags(Set.of("a", "c"))
                                .conferences(Set.of(conference2, Conference.builder("tttt", 1975).build()))
                                .build()
                )));

        WebTestClient
                .bindToController(reportController)
                .build()
                .get()
                .uri(String.format("/api/rest/report/tag?conference_name=%s", conference1.getName()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TagResponse.class).isEqualTo(response);
    }

    @Test
    public void testGetReportTagRequestWithYear() {
        final int conferenceYear = 1971;
        final Conference conference1 = Conference.builder("test", conferenceYear).build();
        final Conference conference2 = Conference.builder("test", 1972).build();
        final Conference conference3 = Conference.builder("test2", conferenceYear).build();

        when(reportService.findAll())
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .tags(Set.of("a", "b"))
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title11")
                                .tags(Set.of("a", "c"))
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title2")
                                .tags(Set.of("a", "c"))
                                .conferences(Set.of(conference2, conference3))
                                .build()
                )));

        WebTestClient
                .bindToController(reportController)
                .build()
                .get()
                .uri(String.format("/api/rest/report/tag?year=%d", conferenceYear))
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class);
    }

    @Test
    public void testGetReportTagRequest() {
        final int conferenceYear = 1971;
        final Conference conference1 = Conference.builder("test", conferenceYear).build();
        final Conference conference2 = Conference.builder("test", 1972).build();
        final Conference conference3 = Conference.builder("test2", conferenceYear).build();

        when(reportService.findAll())
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .tags(Set.of("a", "b"))
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title11")
                                .tags(Set.of("a", "c"))
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title2")
                                .tags(Set.of("a", "c"))
                                .conferences(Set.of(conference2, conference3))
                                .build()
                )));

        WebTestClient
                .bindToController(reportController)
                .build()
                .get()
                .uri("/api/rest/report/tag")
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class);
    }
}

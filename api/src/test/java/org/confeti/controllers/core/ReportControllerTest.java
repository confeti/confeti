package org.confeti.controllers.core;

import org.confeti.config.SecurityConfig;
import org.confeti.controllers.dto.core.ReportResponse;
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
        final ReportResponse response = new ReportResponse(conferenceName, Map.of(year, Map.of("a", 2L, "b", 1L, "c", 1L)));

        when(reportService.findBy(conferenceName, year))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .tags(Set.of("a", "b"))
                                .build(),
                        Report.builder("title2")
                                .tags(Set.of("a", "c"))
                                .build()
                )));
        testGetReportInfoRequestWithConferenceName(
                String.format("/api/rest/report/tag?year=%d&conference_name=%s", year, conferenceName),
                response);
    }

    @Test
    public void testGetReportTagRequestWithConferenceName() {
        final Conference conference1 = Conference.builder("test", 1971).build();
        final Conference conference2 = Conference.builder("test", 1972).build();

        final ReportResponse response = new ReportResponse(conference1.getName(), Map.of(
                1971, Map.of("a", 2L, "b", 1L, "c", 1L),
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
                                .conferences(Set.of(conference2))
                                .build()
                )));
        testGetReportInfoRequestWithConferenceName(String.format("/api/rest/report/tag?conference_name=%s", conference1.getName()), response);
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
                                .conferences(Set.of(conference2))
                                .build()
                )));
        testGetReportInfoRequest(String.format("/api/rest/report/tag?year=%d", conferenceYear));
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
                                .conferences(Set.of(conference2))
                                .build(),
                        Report.builder("title3")
                                .tags(Set.of("a", "c"))
                                .conferences(Set.of(conference3))
                                .build()
                )));

        testGetReportInfoRequest("/api/rest/report/tag");
    }

    @Test
    public void testGetReportLanguageRequestWithYearAndConferenceName() {
        final String conferenceName = "bla";
        final int year = 1970;
        final ReportResponse response = new ReportResponse(conferenceName, Map.of(year, Map.of("RU", 3L, "EN", 1L, "FR", 1L)));

        when(reportService.findBy(conferenceName, year))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .language("RU")
                                .build(),
                        Report.builder("title2")
                                .language("RU")
                                .build(),
                        Report.builder("title3")
                                .language("EN")
                                .build(),
                        Report.builder("title4")
                                .language("RU")
                                .build(),
                        Report.builder("title5")
                                .language("FR")
                                .build()
                )));
        testGetReportInfoRequestWithConferenceName(
                String.format("/api/rest/report/language?year=%d&conference_name=%s", year, conferenceName),
                response);
    }

    @Test
    public void testGetReportLanguageRequestWithConferenceName() {
        final Conference conference1 = Conference.builder("test", 1971).build();
        final Conference conference2 = Conference.builder("test", 1972).build();

        final ReportResponse response = new ReportResponse(conference1.getName(), Map.of(1971, Map.of("RU", 2L),
                1972, Map.of("EN", 1L, "RU", 1L)));

        when(reportService.findBy(conference1.getName()))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .language("RU")
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title11")
                                .language("RU")
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title2")
                                .language("EN")
                                .conferences(Set.of(conference2))
                                .build(),
                        Report.builder("title22")
                                .language("RU")
                                .conferences(Set.of(conference2))
                                .build()
                )));
        testGetReportInfoRequestWithConferenceName(String.format("/api/rest/report/language?conference_name=%s", conference1.getName()), response);
    }

    @Test
    public void testGetReportLanguageRequestWithYear() {
        final int conferenceYear = 1971;
        final Conference conference1 = Conference.builder("test", conferenceYear).build();
        final Conference conference2 = Conference.builder("test", 1972).build();
        final Conference conference3 = Conference.builder("test2", conferenceYear).build();

        when(reportService.findAll())
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .language("RU")
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title11")
                                .language("Ru-ru")
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title3")
                                .language("EN")
                                .conferences(Set.of(conference3))
                                .build()
                )));
        testGetReportInfoRequest(String.format("/api/rest/report/language?year=%d", conferenceYear));
    }

    @Test
    public void testGetReportLanguageRequest() {
        final int conferenceYear = 1971;
        final Conference conference1 = Conference.builder("test", conferenceYear).build();
        final Conference conference2 = Conference.builder("test", 1972).build();
        final Conference conference3 = Conference.builder("test2", conferenceYear).build();

        when(reportService.findAll())
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        Report.builder("title")
                                .language("RU")
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title11")
                                .language("RU")
                                .conferences(Set.of(conference1))
                                .build(),
                        Report.builder("title2")
                                .language("EN")
                                .conferences(Set.of(conference2))
                                .build(),
                        Report.builder("title2")
                                .language("EN")
                                .conferences(Set.of(conference3))
                                .build()
                )));

        testGetReportInfoRequest("/api/rest/report/language");
    }

    private void testGetReportInfoRequestWithConferenceName(final String uri, final ReportResponse expectedResponse) {
        WebTestClient
                .bindToController(reportController)
                .build()
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReportResponse.class).isEqualTo(expectedResponse);
    }

    private void testGetReportInfoRequest(final String uri) {
        WebTestClient
                .bindToController(reportController)
                .build()
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class);
    }

}

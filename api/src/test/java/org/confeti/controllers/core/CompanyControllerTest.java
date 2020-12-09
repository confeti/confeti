package org.confeti.controllers.core;

import org.confeti.config.SecurityConfig;
import org.confeti.controllers.dto.core.CompanyStatResponse;
import org.confeti.service.CompanyService;
import org.confeti.service.ReportStatsService;
import org.confeti.service.dto.Company;
import org.confeti.service.dto.stats.ReportStatsByCompany;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.confeti.controllers.core.StatisticControllerTestUtils.testGetListResponse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {CompanyController.class, ReportStatsService.class, CompanyService.class, SecurityConfig.class})
@RunWith(SpringRunner.class)
public class CompanyControllerTest {
    @MockBean
    private ReportStatsService reportStatsService;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private CompanyController companyController;

    @Test
    public void testGetCompanyStatRequestWithYear() {
        final String companyName = "company-test";
        final int year = 1972;
        final long amount = 5L;

        final CompanyStatResponse expectedResponse = new CompanyStatResponse()
                .setCompanyName(companyName)
                .setYears(Map.of(year, amount));

        when(reportStatsService.countCompanyStatsForYear(companyName, 1972))
                .thenReturn(Mono.just(
                        ReportStatsByCompany.builder()
                                .companyName(companyName)
                                .year(year)
                                .reportTotal(amount).build()));

        WebTestClient
                .bindToController(companyController)
                .build()
                .get()
                .uri(String.format("/api/rest/company/%s/stat?year=%d", companyName, year))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyStatResponse.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetCompanyStatRequestWithoutYearForOneYear() {
        final String companyName = "company-test";
        final int year = 1972;
        final long amount = 5L;
        final CompanyStatResponse expectedResponse = new CompanyStatResponse()
                .setCompanyName(companyName)
                .setYears(Map.of(year, amount));

        when(reportStatsService.countCompanyStats(companyName))
                .thenReturn(Flux.fromIterable(Collections.singletonList(ReportStatsByCompany.builder()
                        .companyName(companyName)
                        .year(year)
                        .reportTotal(amount)
                        .build())));

        WebTestClient
                .bindToController(companyController)
                .build()
                .get()
                .uri(String.format("/api/rest/company/%s/stat", companyName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyStatResponse.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetCompanyStatRequestWithoutYearForMultipleYears() {
        final String companyName = "company-test";
        final int year1 = 1972;
        final long amount1 = 5L;

        final int year2 = 1973;
        final long amount2 = 15L;

        final CompanyStatResponse expectedResponse = new CompanyStatResponse()
                .setCompanyName(companyName)
                .setYears(Map.of(year1, amount1, year2, amount2));

        when(reportStatsService.countCompanyStats(companyName))
                .thenReturn(Flux.fromIterable(Arrays.asList(
                        ReportStatsByCompany.builder()
                                .companyName(companyName)
                                .year(year1)
                                .reportTotal(amount1)
                                .build(),
                        ReportStatsByCompany.builder()
                                .companyName(companyName)
                                .year(year2)
                                .reportTotal(amount2)
                                .build()
                        )
                ));

        WebTestClient
                .bindToController(companyController)
                .build()
                .get()
                .uri(String.format("/api/rest/company/%s/stat", companyName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyStatResponse.class).isEqualTo(expectedResponse);
    }

    @Test
    public void testGetAllCompaniesSuccess() {
        final List<Company> companies = Arrays.asList(
                new Company().setName("company 1"),
                new Company().setName("company 2")
        );

        when(companyService.findAll()).thenReturn(Flux.fromIterable(companies));

        testGetListResponse(companyController, "/api/rest/company", companies, Company[].class);
        verify(companyService).findAll();
    }

    @Test
    public void testGetAllCompaniesStatSuccess() {
        final String companyName1 = "company 1";
        final String companyName2 = "company 2";

        when(reportStatsService.countCompanyStats()).thenReturn(Flux.fromIterable(Arrays.asList(
                ReportStatsByCompany.builder()
                        .year(1972)
                        .companyName(companyName1)
                        .reportTotal(2L)
                        .build(),
                ReportStatsByCompany.builder()
                        .year(1971)
                        .companyName(companyName1)
                        .reportTotal(5L)
                        .build(),
                ReportStatsByCompany.builder()
                        .companyName(companyName2)
                        .year(1973)
                        .reportTotal(4L)
                        .build(),
                ReportStatsByCompany.builder()
                        .companyName(companyName2)
                        .year(1972)
                        .reportTotal(1L)
                        .build(),
                ReportStatsByCompany.builder()
                        .companyName(companyName2)
                        .year(1975)
                        .reportTotal(9L)
                        .build()
        )));

        List<CompanyStatResponse> statResponses = Arrays.asList(
                new CompanyStatResponse()
                        .setCompanyName(companyName1)
                        .setYears(Map.of(1971, 5L, 1972, 2L)),
                new CompanyStatResponse()
                        .setCompanyName(companyName2)
                        .setYears(Map.of(1973, 4L, 1972, 1L, 1975, 9L))
        );

        testGetListResponse(companyController, "/api/rest/company/stat", statResponses, CompanyStatResponse[].class);
    }
}

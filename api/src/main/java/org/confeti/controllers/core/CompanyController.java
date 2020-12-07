package org.confeti.controllers.core;

import lombok.RequiredArgsConstructor;
import org.confeti.controllers.dto.core.CompanyStatResponse;
import org.confeti.service.CompanyService;
import org.confeti.service.ReportStatsService;
import org.confeti.service.dto.stats.ReportStatsByCompany;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.confeti.controllers.ControllersUtils.COMPANY_URI_PARAMETER;
import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.confeti.controllers.ControllersUtils.YEAR_URI_PARAMETER;
import static org.confeti.controllers.core.StatisticControllerUtils.handleBaseGetRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleForAllRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequestWithYear;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/company", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CompanyController {

    private final ReportStatsService reportStatsService;
    private final CompanyService companyService;

    @GetMapping(path = "{" + COMPANY_URI_PARAMETER + "}/stat")
    @ResponseBody
    public Mono<ResponseEntity<?>> handleRequestCompany(
            @PathVariable(COMPANY_URI_PARAMETER) final String companyName) {
        return handleSpecifiedRequest(reportStatsService.countCompanyStats(companyName),
                ReportStatsByCompany::getYear,
                map -> new CompanyStatResponse()
                        .setCompanyName(companyName)
                        .setYears(map));
    }

    @GetMapping(path = "{" + COMPANY_URI_PARAMETER + "}/stat", params = {YEAR_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleRequestCompanyYear(
            @PathVariable(COMPANY_URI_PARAMETER) final String companyName,
            @RequestParam(YEAR_URI_PARAMETER) final int year) {
        return handleSpecifiedRequestWithYear(reportStatsService.countCompanyStatsForYear(companyName, year),
                stat -> new CompanyStatResponse()
                        .setCompanyName(companyName)
                        .setYears(Map.of(year, stat.getReportTotal())));
    }

    @GetMapping(path = "/stat")
    @ResponseBody
    public Mono<ResponseEntity<?>> handleStatRequest() {
        return handleForAllRequest(reportStatsService.countCompanyStats(),
                ReportStatsByCompany::getCompanyName,
                groupedFlux -> groupedFlux.collectMap(ReportStatsByCompany::getYear, ReportStatsByCompany::getReportTotal),
                tuple -> new CompanyStatResponse()
                        .setCompanyName(tuple.getT2())
                        .setYears(tuple.getT1()));
    }

    @GetMapping
    @ResponseBody
    public Mono<ResponseEntity<?>> handleCompanyRequest() {
        return handleBaseGetRequest(companyService.findAll());
    }

}

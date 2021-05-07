package org.confeti.controllers.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.confeti.controllers.dto.core.CompanyStatResponse;
import org.confeti.service.CompanyService;
import org.confeti.service.ReportStatsService;
import org.confeti.service.dto.Company;
import org.confeti.service.dto.stats.ReportStatsByCompany;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.confeti.controllers.ControllersUtils.COMPANY_NAME_URI_PARAMETER;
import static org.confeti.controllers.ControllersUtils.CONTROLLER_MARKER;
import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.confeti.controllers.ControllersUtils.YEAR_URI_PARAMETER;
import static org.confeti.controllers.core.StatisticControllerUtils.handleForAllRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequest;
import static org.confeti.controllers.core.StatisticControllerUtils.handleSpecifiedRequestWithKey;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/company", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final ReportStatsService reportStatsService;
    private final CompanyService companyService;

    @GetMapping(path = "{" + COMPANY_NAME_URI_PARAMETER + "}/stat")
    @ResponseBody
    public Mono<ResponseEntity<Object>> handleStatRequestCompany(
            @PathVariable(COMPANY_NAME_URI_PARAMETER) final String companyName) {
        return handleSpecifiedRequest(reportStatsService.countCompanyStats(companyName),
                ReportStatsByCompany::getYear,
                map -> new CompanyStatResponse()
                        .setCompanyName(companyName)
                        .setYears(map))
                .doOnSubscribe(ign -> logGetRequest(String.format("%s/stat", companyName), ""))
                .doOnError(CompanyController::logError);
    }

    @GetMapping(path = "{" + COMPANY_NAME_URI_PARAMETER + "}/stat", params = {YEAR_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<Object>> handleStatRequestCompanyYear(
            @PathVariable(COMPANY_NAME_URI_PARAMETER) final String companyName,
            @RequestParam(YEAR_URI_PARAMETER) final int year) {
        return handleSpecifiedRequestWithKey(reportStatsService.countCompanyStatsForYear(companyName, year),
                stat -> new CompanyStatResponse()
                        .setCompanyName(companyName)
                        .setYears(Map.of(year, stat.getReportTotal())))
                .doOnError(CompanyController::logError)
                .doOnSubscribe(ign -> logGetRequest(
                        String.format("%s/stat", companyName),
                        String.format("%s=%d", YEAR_URI_PARAMETER, year)));
    }

    @GetMapping(path = "/stat")
    @ResponseBody
    public Flux<CompanyStatResponse> handleStatRequest() {
        return handleForAllRequest(reportStatsService.countCompanyStats(),
                ReportStatsByCompany::getCompanyName,
                groupedFlux -> groupedFlux.collectMap(ReportStatsByCompany::getYear, ReportStatsByCompany::getReportTotal),
                tuple -> new CompanyStatResponse()
                        .setCompanyName(tuple.getT2())
                        .setYears(tuple.getT1()))
                .doOnSubscribe(ign -> logGetRequest("/stat", ""))
                .doOnError(CompanyController::logError);
    }

    @GetMapping
    @ResponseBody
    public Flux<Company> handleCompanyRequest() {
        return companyService.findAll()
                .doOnSubscribe(ign -> logGetRequest("", ""))
                .doOnError(CompanyController::logError);
    }

    private static void logGetRequest(String path, String parameters) {
        log.info(CONTROLLER_MARKER, String.format("GET %s/%s/%s, with params: %s", REST_API_PATH, "company", path, parameters));
    }

    private static void logError(final Throwable e) {
        log.error(CONTROLLER_MARKER, "ERROR is happen: ", e);
    }
}

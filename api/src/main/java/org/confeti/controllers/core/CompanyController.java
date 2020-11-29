package org.confeti.controllers.core;

import lombok.RequiredArgsConstructor;
import org.confeti.controllers.dto.ErrorResponse;
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
import java.util.Objects;

import static org.confeti.controllers.ControllersUtils.COMPANY_URI_PARAMETER;
import static org.confeti.controllers.ControllersUtils.REST_API_PATH;
import static org.confeti.controllers.ControllersUtils.YEAR_URI_PARAMETER;
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
        return reportStatsService.countCompanyStats(companyName)
                .collectMap(ReportStatsByCompany::getYear, ReportStatsByCompany::getReportTotal)
                .map(map -> new CompanyStatResponse()
                        .setCompanyName(companyName)
                        .setYears(map))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    @GetMapping(path = "{" + COMPANY_URI_PARAMETER + "}/stat", params = {YEAR_URI_PARAMETER})
    @ResponseBody
    public Mono<ResponseEntity<?>> handleRequestCompanyYear(
            @PathVariable(COMPANY_URI_PARAMETER) final String companyName,
            @RequestParam(YEAR_URI_PARAMETER) final int year) {
        return reportStatsService.countCompanyStatsForYear(companyName, year)
                .map(stat -> new CompanyStatResponse()
                        .setCompanyName(companyName)
                        .setYears(Map.of(year, stat.getReportTotal())))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

    @GetMapping(path = "/stat")
    @ResponseBody
    public Mono<ResponseEntity<?>> handleStatRequest() {
        return reportStatsService.countCompanyStats()
                .groupBy(ReportStatsByCompany::getCompanyName)
                .flatMap(group ->
                        group
                                .collectMap(ReportStatsByCompany::getYear, ReportStatsByCompany::getReportTotal)
                                .zipWith(Mono.just(Objects.requireNonNull(group.key()))))
                .map(tuple -> new CompanyStatResponse()
                        .setCompanyName(tuple.getT2())
                        .setYears(tuple.getT1()))
                .collectList()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));

    }

    @GetMapping
    @ResponseBody
    public Mono<ResponseEntity<?>> handleCompanyRequest() {
        return companyService.findAll()
                .collectList()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(Exception.class, err -> Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(err.getMessage()))));
    }

}

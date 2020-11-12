package org.confeti.controllers;

import org.confeti.dataobjects.Status;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.confeti.controllers.Controllers.REST_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/status")
public class StatusController {

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Mono<Status> statusRoute() {
        return Mono.just(Status.ok());
    }
}

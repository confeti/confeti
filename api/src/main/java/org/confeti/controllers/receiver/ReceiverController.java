package org.confeti.controllers.receiver;

import org.confeti.dataobjects.Status;
import org.confeti.dataobjects.receiver.Conference;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


import static org.confeti.controllers.Controllers.REST_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = REST_API_PATH + "/conference")
public class ReceiverController {

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<Status> handlePost(@RequestBody Conference conference) {
        System.out.println(conference);
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}

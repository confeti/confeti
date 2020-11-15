package org.confeti.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.confeti.controllers.ControllersTestsUtils.STATUS;


@WebFluxTest
@ContextConfiguration(classes = {StatusController.class})
class StatusControllerTest {

    @Autowired
    private StatusController statusController;

    @Test
    public void testStatusRoute() {
        WebTestClient
                .bindToController(statusController)
                .build()
                .get().uri("/api/rest/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(STATUS);
    }
}

package org.confeti.routers;

import org.confeti.controllers.StatusController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@WebFluxTest
@ContextConfiguration(classes = {StatusController.class})
class StatusControllerTest {

    private static final String STATUS = "{\"status\":\"OK\"}";

    @Autowired
    private RouterFunction<ServerResponse> statusRoute;

    @Test
    public void testStatusRoute() {
        WebTestClient
                .bindToRouterFunction(statusRoute)
                .build()
                .get().uri("/api/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(STATUS);
    }
}

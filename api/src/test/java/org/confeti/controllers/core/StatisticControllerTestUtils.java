package org.confeti.controllers.core;

import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.confeti.controllers.ControllersTestsUtils.assertEqualsList;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StatisticControllerTestUtils {

    public static <T> void testGetListResponse(
            final Object controller,
            final String uri,
            final List<T> expectedResponse,
            final Class<T> clazz) {

        // We couldn't warranty ordering
        EntityExchangeResult<List> response = WebTestClient
                .bindToController(controller)
                .build()
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class).returnResult();

        assertNotNull(response.getResponseBody());
        List body = response.getResponseBody();
        assertEqualsList(body, expectedResponse, clazz);
    }
}

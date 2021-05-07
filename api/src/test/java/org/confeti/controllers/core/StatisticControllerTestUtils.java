package org.confeti.controllers.core;

import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class StatisticControllerTestUtils {

    // Not instantiable
    private StatisticControllerTestUtils() {
        throw new AssertionError();
    }

    public static <T> void testGetListResponse(
            final Object controller,
            final String uri,
            final List<T> expectedResponse,
            final Class<T[]> clazz) {

        // We couldn't warranty ordering
        EntityExchangeResult<T[]> response = WebTestClient
                .bindToController(controller)
                .build()
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(clazz).returnResult();

        assertNotNull(response.getResponseBody());
        assertThat(Arrays.asList(response.getResponseBody()), containsInAnyOrder(expectedResponse.toArray()));
    }

    public static void testErrorResponse(final Object controller, final String uri, int status) {
        WebTestClient
                .bindToController(controller)
                .build()
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isEqualTo(status);
    }
}

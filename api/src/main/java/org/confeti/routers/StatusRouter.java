package org.confeti.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
public class StatusRouter {
    private static final String STATUS = "{\"status\":\"OK\"}";

    @Bean
    public RouterFunction<ServerResponse> statusRoute() {
        return RouterFunctions
                .route()
                .GET("/api/status", request ->
                        ServerResponse
                                .ok()
                                .contentType(APPLICATION_JSON)
                                .bodyValue(STATUS))
                .build();
    }
}

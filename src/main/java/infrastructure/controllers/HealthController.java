package infrastructure.controllers;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class HealthController {

    private final DatabaseClient databaseClient;

    public HealthController(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        return databaseClient
                .sql("SELECT 1")
                .fetch()
                .first()
                .map(result -> Map.of(
                        "status", "UP",
                        "database", "UP",
                        "app", "ParcialApplication"
                ))
                .onErrorReturn(Map.of(
                        "status", "DOWN",
                        "database", "DOWN",
                        "app", "ParcialApplication"
                ));
    }
}
package com.nana.logsentry.tracing.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BServiceClient { // WebClient로 B 서비스 호출

    private final WebClient webClient;

    public Mono<String> callB() {
        return webClient.get()
                .uri("http://localhost:8081/b/hello")
                .retrieve()
                .bodyToMono(String.class);
    }
}
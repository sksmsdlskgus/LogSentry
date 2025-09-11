package com.nana.logsentry.tracing.client;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BServiceClient { // WebClient로 B 서비스 호출

    private final WebClient webClient;

    @Observed(name = "bServiceClient.callB", contextualName = "WebClient → B")
    public Mono<String> callB() {
        return webClient.get()
                .uri("/b/hello")     // 상대경로로 전환 -> service-b 8082서버 호출
                .retrieve()
                .bodyToMono(String.class);
    }
}

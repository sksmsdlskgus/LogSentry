package com.nana.logsentry.config.tracing;


import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TracingWebClientConfig {

    private final BProps props;
    private final Tracer tracer;

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(props.baseUrl())
                .filter((request, next) -> {
                    var context = tracer.currentTraceContext().context();
                    if (context != null) {
                        log.info("[Tracing] traceId={}, spanId={}", context.traceId(), context.spanId());
                    }
                    return next.exchange(request);
                })
                .build();
    }

}
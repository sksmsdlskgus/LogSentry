package com.nana.logsentry.config.tracing;


import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class TracingWebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder, Tracer tracer) {
        return builder
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
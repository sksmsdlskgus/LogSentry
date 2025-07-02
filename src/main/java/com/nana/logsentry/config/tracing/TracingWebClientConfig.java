package com.nana.logsentry.config.tracing;


import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class TracingWebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(TracingWebClientConfig.class);

    @Bean
    public WebClient webClient(WebClient.Builder builder, Tracer tracer, Propagator propagator) {
        return builder
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
                .filter((request, next) -> {
                    // 수동으로 trace 정보를 헤더에 삽입
                    return next.exchange(
                            ClientRequest.from(request)
                                    .headers(httpHeaders -> {
                                        var context = tracer.currentTraceContext().context();
                                        if (context != null) {
                                            propagator.inject(context, httpHeaders, HttpHeaders::set);
                                            log.info("[Tracing] WebClient 요청에 traceId={} spanId={}",
                                                    context.traceId(), context.spanId());
                                        }
                                    })
                                    .build()
                    );
                })
                .build();
    }
}
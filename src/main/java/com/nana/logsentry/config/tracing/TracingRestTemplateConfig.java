package com.nana.logsentry.config.tracing;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TracingRestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(Tracer tracer, Propagator propagator) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new TracingRestTemplateInterceptor(tracer, propagator));
        return restTemplate;
    }
}
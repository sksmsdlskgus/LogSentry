package com.nana.logsentry.config.tracing;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

import java.io.IOException;

public class TracingRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TracingRestTemplateInterceptor.class);

    private final Tracer tracer;
    private final Propagator propagator;

    public TracingRestTemplateInterceptor(Tracer tracer, Propagator propagator) {
        this.tracer = tracer;
        this.propagator = propagator;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        var context = tracer.currentTraceContext().context();

        if (context != null) {
            log.info("[TracingRestTemplateInterceptor] traceId={}, spanId={}", context.traceId(), context.spanId());
            propagator.inject(context, request.getHeaders(), HttpHeaders::set);
        }

        return execution.execute(request, body);
    }
}
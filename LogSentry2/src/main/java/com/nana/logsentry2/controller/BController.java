package com.nana.logsentry2.controller;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/b")
public class BController {

    private static final Logger log = LoggerFactory.getLogger(BController.class);

    @Observed(name = "bController.hello", contextualName = "HTTP /b/hello")
    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> hello() {
        log.info("[B] /b/hello 요청");
        return Mono.just("Hello from B!");
    }
}

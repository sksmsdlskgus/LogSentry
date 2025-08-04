package com.nana.logsentry.tracing.controller;

import com.nana.logsentry.kafka.BizLogger;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/b")
public class BController { // 호출 대상

    private static final Logger log = LoggerFactory.getLogger(BController.class);

    @Observed(name = "bController.hello", contextualName = "HTTP /b/hello")
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.fromSupplier(() -> {
            BizLogger.info("[BController] B 서비스 응답"); // Kafka 전송
            log.info("[BController] B 서비스 응답");     // 로컬/파일
            return "Hello from B!";
        });
    }
}
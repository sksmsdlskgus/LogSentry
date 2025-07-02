package com.nana.logsentry.tracing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/b")
public class BController { // 호출 대상

    private static final Logger log = LoggerFactory.getLogger(BController.class);

    @GetMapping("/hello")
    public String hello() {
        log.info("[BController] B 서비스 응답");
        return "Hello from B!";
    }
}
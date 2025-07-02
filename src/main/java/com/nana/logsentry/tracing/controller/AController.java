package com.nana.logsentry.tracing.controller;

import com.nana.logsentry.tracing.service.AService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/a")
@RequiredArgsConstructor
public class AController { // 호출 시작점

    private final AService aService;

    @GetMapping("/call-b")
    public String callBFromA() {
        return aService.doBusiness();
    }
}

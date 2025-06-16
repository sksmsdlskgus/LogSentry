package com.nana.logsentry.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogAnalyzerController {

    private static final Logger log = LoggerFactory.getLogger(LogAnalyzerController.class);

    @GetMapping("/test")
    public String testLogging() {
        log.info("🔥 로그 테스트 - INFO 레벨");
        log.warn("⚠️ 로그 테스트 - WARN 레벨");
        log.error("💥 로그 테스트 - ERROR 레벨", new RuntimeException("예외 예시"));

        return "로그 발생 완료";
    }
}

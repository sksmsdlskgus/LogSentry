package com.nana.logsentry;

import com.nana.logsentry.kafka.BizLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    // http://localhost:8081/test
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("✅ 로컬 전용 로그");
        BizLogger.info("✅ Kafka 전송용 로그: /test 호출됨");
        return ResponseEntity.ok("트레이싱 확인용 응답 ok");
    }

}

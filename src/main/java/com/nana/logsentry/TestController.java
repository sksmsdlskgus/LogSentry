package com.nana.logsentry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    // http://localhost:8081/test
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("✅ 로그 트레이싱 테스트 요청 수신됨");
        return ResponseEntity.ok("트레이싱 확인용 응답");
    }

}

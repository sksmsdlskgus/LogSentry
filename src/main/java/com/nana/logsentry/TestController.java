package com.nana.logsentry;

import com.nana.logsentry.kafka.BizLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @Operation(
            summary = "Kafka 로그 전송 테스트 조회",
            description = "로컬/파일 로그와 Kafka 전송 로그를 동시에 기록해 정상 동작 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상적으로 로그 전송이 완료되었습니다."),
                    @ApiResponse(responseCode = "500", description = "로그 전송 중 서버 오류가 발생했습니다.")
            }
    )
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("✅ 로컬 전용 로그");
        BizLogger.info("✅ Kafka 전송용 로그: /test 호출됨");
        return ResponseEntity.ok("트레이싱 확인용 응답 ok");
    }

    @Operation(
            summary = "Kafka 로그 전송 테스트 조회 (파라미터 포함)",
            description = "사용자가 전달한 name 파라미터를 받아 인사 메시지를 반환합니다. "
                    + "로컬/파일 로그와 Kafka 전송 로그를 모두 기록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상적으로 인사 메시지를 반환했습니다."),
                    @ApiResponse(responseCode = "500", description = "요청 처리 중 서버 오류가 발생했습니다.")
            }
    )
    @GetMapping("/hello")
    public ResponseEntity<String> hello(
            @Parameter(description = "사용자 이름 (예: nana)")
            @RequestParam String name) {
        log.info("Hello API 호출: {}", name);
        BizLogger.info("Hello API 호출: {}", name);
        return ResponseEntity.ok("hi " + name);
    }

    @Operation(
            summary = "에러 로그 발생 테스트 조회",
            description = "강제 RuntimeException을 발생시켜 ERROR 레벨 로그와 스택트레이스를 확인합니다. "
                    + "Kafka 전송 로그와 Slack 알림까지 트리거됩니다.",
            responses = {
                    @ApiResponse(responseCode = "500", description = "강제 예외 발생으로 에러 로그가 전송되었습니다.")
            }
    )
    @GetMapping("/error-test")
    public ResponseEntity<String> errorTest() {
        try {
            throw new RuntimeException("💥 테스트용 강제 예외 발생!");
        } catch (RuntimeException e) {
            log.error("🚨 로컬 전용 ERROR 로그 발생", e);
            BizLogger.error("🚨 Kafka 전송용 ERROR 로그 발생: /error-test 호출됨", e);
            return ResponseEntity.internalServerError().body("error");
        }
    }

    @Operation(
            summary = "중요 장애 발생 테스트 조회",
            description = "강제 RuntimeException을 발생시켜 ALERT 마커를 통한 Slack 즉시 알림을 발생시킵니다. "
                    + "중요 장애 상황(critical error) 시나리오를 테스트할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "500", description = "중요 장애 발생으로 에러 로그 및 Slack 알림이 전송되었습니다.")
            }
    )
    @GetMapping("/critical-error")
    public ResponseEntity<String> criticalErrorTest() {
        try {
            throw new RuntimeException("🔥 중요 장애 발생!");
        } catch (RuntimeException e) {
            log.error("로컬 전용 ERROR 로그", e);
            BizLogger.alert("🔥 ALERT: 중요 장애 발생! /critical-error 호출됨", e);
            return ResponseEntity.internalServerError().body("critical error");
        }
    }

}

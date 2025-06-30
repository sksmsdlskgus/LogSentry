package com.nana.logsentry.log.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogFilterRequestDto {

    @Schema(description = "조회 시작 날짜 (yyyy-MM-dd)", example = "2025-06-01", defaultValue = "최근 7일 전")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "조회 종료 날짜 (yyyy-MM-dd)", example = "2025-06-30")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Schema(description = "로그 레벨", example = "INFO", allowableValues = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR"})
    private String level;

    @Schema(description = "요청 IP 주소", example = "192.168.0.1")
    private String clientIp;

    @Schema(description = "요청 URI (시작 문자열 매칭)", example = "/api/logs")
    private String uri;

    @Schema(description = "HTTP 메서드", example = "GET", allowableValues = {"GET", "POST", "PUT", "DELETE", "PATCH"})
    private String method;

    @Schema(description = "HTTP 상태 코드", example = "200", allowableValues = {"200", "201", "204", "400", "401", "403", "404", "500"})
    private String status;

    @Schema(description = "Trace ID (요청 흐름 추적용)", example = "abc123")
    private String traceId;

    @Schema(description = "User ID (로그인 사용자 식별)", example = "42")
    private String userId;
}

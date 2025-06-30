package com.nana.logsentry.log.dto.response;

import com.nana.logsentry.model.LogEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogEntryResponseDto {

    @Schema(description = "로그 발생 시각", example = "2025-06-25 10:00:00")
    private String timestamp;

    @Schema(description = "Trace ID", example = "xyz001")
    private String traceId;

    @Schema(description = "유저 ID", example = "13")
    private String userId;

    @Schema(description = "요청 URI", example = "/api/data")
    private String uri;

    @Schema(description = "HTTP 메서드", example = "GET")
    private String method;

    @Schema(description = "HTTP 상태 코드", example = "200", allowableValues = {"200", "201", "204", "400", "401", "403", "404", "500"})
    private String status;

    @Schema(description = "IP 주소", example = "127.0.0.1")
    private String clientIp;

    @Schema(description = "User-Agent", example = "JUnit")
    private String userAgent;

    @Schema(description = "로그 레벨", example = "INFO")
    private String level;

    @Schema(description = "원본 메시지", example = "200 데이터 조회 성공")
    private String message;


    public static LogEntryResponseDto from(LogEntry entry) {
        // 응답 DTO, Setter대신 생성자/factory method로 변환
        String status = null;
        String message = entry.getMessage();

        // 상태코드 추출: 메시지 앞에 있는 숫자만 파싱
        if (message != null && message.length() >= 4 && Character.isDigit(message.charAt(0))) {
            status = message.substring(0, 3);
        }

        return new LogEntryResponseDto(
                entry.getTimestamp().toString(),
                entry.getTraceId(),
                entry.getUserId(),
                entry.getUri(),
                entry.getMethod(),
                status,
                entry.getClientIp(),
                entry.getUserAgent(),
                entry.getLevel(),
                message
        );
    }
}

package com.nana.logsentry.excel.service;

import com.nana.logsentry.util.parser.LogParserUtils;
import com.nana.logsentry.model.LogEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogExportService 단위 테스트")
class LogExportServiceTest {
    // <메서드명>_should<기대 결과>_when<조건>

    private final LogExportService service = new LogExportService();
    private final LogParserUtils parser = new LogParserUtils();

    @Test
    @DisplayName("유효한 로그 라인 파싱 시 LogEntry 객체 반환")
    void parse_shouldReturnLogEntry_whenLogIsValid() {

        String line = "timestamp=2025-06-25 12:00:00, traceId=abc123, userId=1, " +
                "uri=/api/test, method=GET, clientIp=127.0.0.1, userAgent=JUnit, " +
                "level=INFO, logger=com.example.TestLogger, thread=main, message=요청 처리 완료";

        LogEntry entry = parser.parse(line);

        assertNotNull(entry);
        assertEquals(LocalDateTime.of(2025, 6, 25, 12, 0), entry.getTimestamp());
        assertEquals("abc123", entry.getTraceId());
        assertEquals("1", entry.getUserId());
        assertEquals("/api/test", entry.getUri());
        assertEquals("GET", entry.getMethod());
        assertEquals("127.0.0.1", entry.getClientIp());
        assertEquals("JUnit", entry.getUserAgent());
        assertEquals("INFO", entry.getLevel());
        assertEquals("요청 처리 완료", entry.getMessage());

    }

    @Test
    @DisplayName("IP별로 요청 수를 정확히 집계")
    void getRequestsByIp_shouldGroupByIpCorrectly() {
        List<LogEntry> logs = List.of(
                new LogEntry(LocalDateTime.of(2025, 6, 25, 12, 0), "INFO", "t1", "u1", "/a", "GET", "1.1.1.1", "agent", "msg"),
                new LogEntry(LocalDateTime.of(2025, 6, 25, 12, 1), "INFO", "t2", "u2", "/b", "POST", "1.1.1.1", "agent", "msg"),
                new LogEntry(LocalDateTime.of(2025, 6, 25, 12, 2), "INFO", "t3", "u3", "/c", "GET", "2.2.2.2", "agent", "msg"),
                new LogEntry(LocalDateTime.of(2025, 6, 25, 12, 3), "INFO", "t4", "u4", "/d", "POST", "3.3.3.3", "agent", "msg"),
                new LogEntry(LocalDateTime.of(2025, 6, 25, 12, 4), "INFO", "t5", "u5", "/e", "GET", "3.3.3.3", "agent", "msg")
        );

        Map<String, Long> result = service.getRequestsByIp(logs);

        assertEquals(2, result.get("1.1.1.1"));
        assertEquals(1, result.get("2.2.2.2"));
        assertEquals(2, result.get("3.3.3.3"));
    }

}

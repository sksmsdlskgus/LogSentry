package com.nana.logsentry.log.service;

import com.nana.logsentry.log.dto.request.LogFilterRequestDto;
import com.nana.logsentry.log.dto.response.TopIpStatResponseDto;
import com.nana.logsentry.log.dto.response.TopUriStatResponseDto;
import com.nana.logsentry.model.LogEntry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LogAnalyzerService 단위 테스트")
class LogAnalyzerServiceTest {

    private LogAnalyzerService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // logs/text 경로 생성
        Path logTextDir = tempDir.resolve("logs/text");
        Files.createDirectories(logTextDir);

        // 테스트용 로그 파일 생성
        String date = "2025-06-29";
        Path logFile = logTextDir.resolve("client-requests." + date + ".log");
        Files.write(logFile, List.of(
                "timestamp=2025-06-29T12:00:00+09:00, traceId=abc123, spanId=def001, userId=u1, uri=/api/a, method=GET, clientIp=1.1.1.1, userAgent=TestAgent, level=INFO, logger=test, thread=main, message=500 서버 오류 발생",
                "timestamp=2025-06-29T12:01:00+09:00, traceId=abc124, spanId=def002, userId=u2, uri=/api/b, method=POST, clientIp=2.2.2.2, userAgent=TestAgent, level=INFO, logger=test, thread=main, message=200 요청 성공",
                "timestamp=2025-06-29T12:02:00+09:00, traceId=abc125, spanId=def003, userId=u3, uri=/api/a, method=GET, clientIp=1.1.1.1, userAgent=TestAgent, level=INFO, logger=test, thread=main, message=200 요청 성공"
        ));


        // 서비스 인스턴스를 임시 경로 기준으로 재정의
        service = new LogAnalyzerService(logTextDir.toString()) {
            @Override
            protected Path resolveLogFilePath(String date) {
                return tempDir.resolve("logs/text/client-requests." + date + ".log");
            }

            @Override
            protected String getLogDir() {
                return tempDir.resolve("logs/text").toString();
            }
        };
    }

    @Test
    @DisplayName("최근 로그 조회 시 최대 100개 반환")
    void getLatestLogs_shouldReturnLimitedSortedLogs() {
        List<LogEntry> logs = service.getLatestLogs("2025-06-29");
        assertEquals(3, logs.size());
        assertTrue(logs.get(0).getTimestamp().isAfter(logs.get(1).getTimestamp()));
    }

    @Test
    @DisplayName("TOP5 IP 조회")
    void getTopLogIp_shouldReturnCorrectTopIps() {
        List<TopIpStatResponseDto> result = service.getTopLogIp(LocalDate.of(2025, 6, 29));
        assertEquals(2, result.size());
        assertEquals("1.1.1.1", result.get(0).getClientIp());
        assertEquals(2, result.get(0).getCount());
    }

    @Test
    @DisplayName("TOP5 URI 조회")
    void getTopLogUri_shouldReturnCorrectTopUris() {
        List<TopUriStatResponseDto> result = service.getTopLogUri(LocalDate.of(2025, 6, 29));
        assertEquals(2, result.size());
        assertEquals("/api/a", result.get(0).getUri());
        assertEquals(2, result.get(0).getCount());
    }

    @Test
    @DisplayName("로그 파일 날짜 목록 반환")
    void getLogFileDate_shouldReturnCorrectDates() {
        List<String> dates = service.getLogFileDate();
        assertTrue(dates.contains("2025-06-29"));
    }

    @Test
    @DisplayName("필터: 날짜 + IP + 상태코드 조건에 맞는 로그만 필터링")
    void filterLogs_shouldReturnFilteredResult() {
        LogFilterRequestDto req = new LogFilterRequestDto();
        req.setStartDate(LocalDate.of(2025, 6, 29));
        req.setEndDate(LocalDate.of(2025, 6, 29));
        req.setClientIp("1.1.1.1");
        req.setStatus("500");

        List<LogEntry> result = service.filterLogs(req);

        assertEquals(1, result.size());
        assertEquals("1.1.1.1", result.get(0).getClientIp());
        assertTrue(result.get(0).getMessage().contains("500"));
    }

    @Test
    @DisplayName("필터: spanId로 정확히 필터링")
    void filterLogs_shouldFilterBySpanId() {
        LogFilterRequestDto req = new LogFilterRequestDto();
        req.setStartDate(LocalDate.of(2025, 6, 29));
        req.setEndDate(LocalDate.of(2025, 6, 29));
        req.setSpanId("def002");

        List<LogEntry> result = service.filterLogs(req);

        assertEquals(1, result.size());
        assertEquals("def002", result.get(0).getSpanId());
    }

}

package com.nana.logsentry.log.service;

import com.nana.logsentry.log.dto.TopIpStatDto;
import com.nana.logsentry.log.dto.TopUriStatDto;
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
                "timestamp=2025-06-29 12:00:00, traceId=abc123, userId=u1, uri=/api/a, method=GET, clientIp=1.1.1.1, userAgent=TestAgent, level=INFO, logger=test, thread=main, message=요청 성공",
                "timestamp=2025-06-29 12:01:00, traceId=abc124, userId=u2, uri=/api/b, method=POST, clientIp=2.2.2.2, userAgent=TestAgent, level=INFO, logger=test, thread=main, message=요청 성공",
                "timestamp=2025-06-29 12:02:00, traceId=abc125, userId=u3, uri=/api/a, method=GET, clientIp=1.1.1.1, userAgent=TestAgent, level=INFO, logger=test, thread=main, message=요청 성공"
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


}

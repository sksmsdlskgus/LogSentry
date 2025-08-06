package com.nana.logsentry.log.service;

import com.nana.logsentry.log.dto.request.LogFilterRequestDto;
import com.nana.logsentry.log.dto.response.TopIpStatResponseDto;
import com.nana.logsentry.log.dto.response.TopUriStatResponseDto;
import com.nana.logsentry.model.LogEntry;
import com.nana.logsentry.util.parser.LogParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class LogAnalyzerService {

    private final LogParserUtils parser;
    private final String logDir;

    private static final String LOG_PREFIX = "client-requests.";
    private static final String LOG_SUFFIX = ".log";

    // 기본 생성자: 운영 환경에서 사용됨
    // logs/text 디렉토리와 기본 파서 사용
    public LogAnalyzerService() {
        this("logs/text", new LogParserUtils());
    }

    // 테스트용 또는 커스텀 로그 경로를 지정할 수 있는 생성자
    // 로그 디렉토리만 외부에서 주입받고, 파서는 기본 사용
    public LogAnalyzerService(String logDir) {
        this(logDir, new LogParserUtils());
    }

    // 완전한 주입용 생성자
    // 로그 디렉토리와 파서 모두 외부에서 주입 가능 (유닛 테스트 등 유연한 확장을 위함)
    public LogAnalyzerService(String logDir, LogParserUtils parser) {
        this.logDir = logDir;
        this.parser = parser;
    }

    protected String getLogDir() {
        return logDir;
    }

    // 최신 로그 조회 API (오늘 기준으로 최근 100개 가져오기)
    public List<LogEntry> getLatestLogs(String date) {
        return parseLogEntries(date).stream()
                .sorted(Comparator.comparing(LogEntry::getTimestamp).reversed())
                .limit(100)
                .toList();
    }

    // TOP5 요청 IP 조회 (30일 기준으로 5개 가져오기)
    public List<TopIpStatResponseDto> getTopLogIp(LocalDate baseDate) {
        List<LogEntry> allEntries = collectEntriesForPastDays(baseDate, 30);

        return allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getClientIp, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopIpStatResponseDto(e.getKey(), Math.toIntExact(e.getValue())))
                .collect(Collectors.toList());
    }

    // TOP5 요청 URI 조회 (30일 기준으로 5개 가져오기)
    public List<TopUriStatResponseDto> getTopLogUri(LocalDate baseDate) {
        List<LogEntry> allEntries = collectEntriesForPastDays(baseDate, 30);

        return allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getUri, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopUriStatResponseDto(e.getKey(), Math.toIntExact(e.getValue())))
                .collect(Collectors.toList());
    }

    // 로그 파일 날짜 목록 조회
    public List<String> getLogFileDate() {
        try {
            return Files.list(Paths.get(logDir))
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.matches("client-requests\\.\\d{4}-\\d{2}-\\d{2}\\.log"))
                    .map(name -> name.substring(LOG_PREFIX.length(), name.length() - LOG_SUFFIX.length()))
                    .sorted() // 필요 시 정렬
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("로그 디렉토리 조회 실패: " + logDir, e);
        }
    }

    // 날짜 기준으로 30일 조회
    private List<LogEntry> collectEntriesForPastDays(LocalDate baseDate, int days) {
        List<LogEntry> entries = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = baseDate.minusDays(i);
            entries.addAll(parseLogEntries(date.toString()));
        }
        return entries;
    }

    // 파싱할 경로 날짜 조회
    protected Path resolveLogFilePath(String date) {
        return Paths.get(logDir, LOG_PREFIX + date + LOG_SUFFIX);
    }

    // 공통 파싱 후 List 반환
    private List<LogEntry> parseLogEntries(String date) {
        Path logPath = resolveLogFilePath(date);

        if (!Files.exists(logPath)) {
            return Collections.emptyList();
        }

        try {
            return Files.lines(logPath)
                    .map(parser::parse)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", logPath, e);
            return Collections.emptyList();
        }
    }


    // 전체 필터링 조회
    public List<LogEntry> filterLogs(LogFilterRequestDto req) {

        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw new IllegalArgumentException("날짜는 필수입니다.");
        }

        List<LogEntry> parsed = parseLogEntriesBetween(req.getStartDate(), req.getEndDate());

        Stream<LogEntry> stream = parsed.stream();

        if (req.getLevel() != null && !req.getLevel().isBlank())
            stream = stream.filter(log -> log.getLevel().equalsIgnoreCase(req.getLevel()));

        if (req.getClientIp() != null && !req.getClientIp().isBlank())
            stream = stream.filter(log -> log.getClientIp().equals(req.getClientIp()));

        if (req.getUri() != null && !req.getUri().isBlank())
            stream = stream.filter(log -> log.getUri().startsWith(req.getUri()));

        if (req.getMethod() != null && !req.getMethod().isBlank())
            stream = stream.filter(log -> log.getMethod().equalsIgnoreCase(req.getMethod()));

        if (req.getStatus() != null && !req.getStatus().isBlank())
            stream = stream.filter(log -> log.getMessage().contains(req.getStatus()));

        if (req.getTraceId() != null && !req.getTraceId().isBlank())
            stream = stream.filter(log -> log.getTraceId().equals(req.getTraceId()));

        if (req.getSpanId() != null && !req.getSpanId().isBlank())
            stream = stream.filter(log -> log.getSpanId().equals(req.getSpanId()));

        if (req.getUserId() != null && !req.getUserId().isBlank())
            stream = stream.filter(log -> log.getUserId().equals(req.getUserId()));

        return stream
                .sorted(Comparator.comparing(LogEntry::getTimestamp).reversed())
                .limit(100)
                .collect(Collectors.toList());
    }

    // 기간별 필터링 List 반환
    public List<LogEntry> parseLogEntriesBetween(LocalDate startDate, LocalDate endDate) {
        List<LogEntry> allEntries = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.toString();
            try {
                allEntries.addAll(
                        Optional.ofNullable(parseLogEntries(dateStr)).orElse(Collections.emptyList())
                );
            } catch (Exception e) {
                log.error("로그 파싱 중 예외 발생 ({}): {}", dateStr, e.getMessage());
            }
        }

        return allEntries;
    }


}

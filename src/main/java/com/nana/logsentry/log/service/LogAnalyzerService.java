package com.nana.logsentry.log.service;

import com.nana.logsentry.log.dto.TopIpStatDto;
import com.nana.logsentry.log.dto.TopUriStatDto;
import com.nana.logsentry.model.LogEntry;
import com.nana.logsentry.util.parser.LogParserUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {

    private final LogParserUtils parser = new LogParserUtils();

    private static final String LOG_DIR = "logs/text";
    private static final String LOG_PREFIX = "client-requests.";
    private static final String LOG_SUFFIX = ".log";

    // 최신 로그 조회 API (오늘 기준으로 최근 100개 가져오기)
    public List<LogEntry> getLatestLogs(String date) {
        return parseLogEntries(date).stream()
                .sorted(Comparator.comparing(LogEntry::getTimestamp).reversed())
                .limit(100)
                .toList();
    }

    // TOP5 요청 IP 조회 (30일 기준으로 5개 가져오기)
    public List<TopIpStatDto> getTopLogIp(LocalDate baseDate) {
        List<LogEntry> allEntries = collectEntriesForPastDays(baseDate, 30);

        return allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getClientIp, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopIpStatDto(e.getKey(), Math.toIntExact(e.getValue())))
                .collect(Collectors.toList());
    }

    // TOP5 요청 URI 조회 (30일 기준으로 5개 가져오기)
    public List<TopUriStatDto> getTopLogUri(LocalDate baseDate) {
        List<LogEntry> allEntries = collectEntriesForPastDays(baseDate, 30);

        return allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getUri, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopUriStatDto(e.getKey(), Math.toIntExact(e.getValue())))
                .collect(Collectors.toList());
    }

    // 로그 파일 날짜 목록 조회
    public List<String> getLogFileDate() {
        try {
            return Files.list(Paths.get(LOG_DIR))
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.startsWith(LOG_PREFIX) && name.endsWith(LOG_SUFFIX))
                    .map(name -> name.substring(LOG_PREFIX.length(), name.length() - LOG_SUFFIX.length()))
                    .sorted() // 필요 시 정렬
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("로그 디렉토리 조회 실패: " + LOG_DIR, e);
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
    private Path resolveLogFilePath(String date) {
        return Paths.get(LOG_DIR, LOG_PREFIX + date + LOG_SUFFIX);
    }

    // 공통 파싱 후 List 반환
    private List<LogEntry> parseLogEntries(String date) {
        Path logPath = resolveLogFilePath(date);

        if (!Files.exists(logPath)) {
            return Collections.emptyList(); // 해당 날짜의 로그 파일이 없으면 빈 리스트 반환
        }

        try {
            return Files.lines(logPath)
                    .map(parser::parse)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList(); // 하나의 파일 읽기에 실패해도 전체 중단은 방지
        }
    }

}

package com.nana.logsentry.log.service;

import com.nana.logsentry.log.dto.TopIpStatDto;
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

    // 최신 로그 조회 API (오늘 기준으로 최근 100개 가져오기)
    public List<LogEntry> lastLogFile(String date) {
        return parseLogEntries(date).stream()
                .sorted(Comparator.comparing(LogEntry::getTimestamp).reversed())
                .limit(100)
                .toList();
    }

    // TOP5 요청 IP 조회 (30일 기준으로 5개 가져오기)
    public List<TopIpStatDto> getTopLogIp(LocalDate baseDate) {
        List<LogEntry> allEntries = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            LocalDate date = baseDate.minusDays(i);
            allEntries.addAll(parseLogEntries(date.toString()));
        }

        return allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getClientIp, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopIpStatDto(e.getKey(), Math.toIntExact(e.getValue())))
                .collect(Collectors.toList());
    }


    // 공통 파싱 후 List 반환
    private List<LogEntry> parseLogEntries(String date) {
        String logFile = String.format("logs/text/client-requests.%s.log", date);
        Path logPath = Paths.get(logFile);

        if (!Files.exists(logPath)) {
            return new ArrayList<>(); // 해당 날짜의 로그 파일이 없으면 빈 리스트 반환
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

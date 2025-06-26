package com.nana.logsentry.excel.service;

import com.nana.logsentry.model.LogEntry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "timestamp=(.*?), traceId=(.*?), userId=(.*?),\\s*" +
                    "uri=(.*?), method=(.*?), clientIp=(.*?), userAgent=(.*?), " +
                    "level=(.*?), logger=(.*?), thread=(.*?), message=(.*)"
    );

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 지정된 날짜의 로그 파일을 파싱하여 LogEntry 리스트로 반환
    public List<LogEntry> parseLogFile(String date) {
        String logFile = String.format("logs/text/client-requests.%s.log", date);
        Path logPath = Paths.get(logFile);

        if (!Files.exists(logPath)) {
            return new ArrayList<>(); // 해당 날짜의 로그 파일이 없으면 빈 리스트 반환
        }

        try {
            return Files.lines(logPath)
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("로그 파일 읽기 실패: " + logFile, e);
        }
    }

    // @param line 로그 한 줄을 LogEntry 객체로 파싱
    private LogEntry parseLine(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            return new LogEntry(
                    LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMAT), // timestamp
                    matcher.group(8).trim(), // level
                    matcher.group(2).trim(), // traceId
                    matcher.group(3).trim(), // userId
                    matcher.group(4).trim(), // uri
                    matcher.group(5).trim(), // method
                    matcher.group(6).trim(), // clientIp
                    matcher.group(7).trim(), // userAgent
                    matcher.group(11).trim() // message
            );
        }
        return null;  // 파싱 실패 시 null
    }

}
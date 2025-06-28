package com.nana.logsentry.log.service;

import com.nana.logsentry.model.LogEntry;
import com.nana.logsentry.util.parser.LogParserUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {

    private final LogParserUtils parser = new LogParserUtils();

    // 최신 로그 조회 API (오늘 기준으로 최근 100개 가져오기)
    public List<LogEntry> lastLogFile(String date) {
        String logFile = String.format("logs/text/client-requests.%s.log", date);
        Path logPath = Paths.get(logFile);

        try {
            return Files.lines(logPath)
                    .map(parser::parse)
                    .filter(Objects::nonNull)
                    .limit(100) // 오늘 기준으로 100개 제한
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("로그 파일 읽기 실패: " + logFile, e);
        }
    }


}

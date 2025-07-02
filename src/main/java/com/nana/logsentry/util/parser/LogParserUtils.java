package com.nana.logsentry.util.parser;

import com.nana.logsentry.model.LogEntry;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LogParserUtils {

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "timestamp=(.*?), traceId=(.*?), spanId=(.*?), userId=(.*?),\\s*" +
                    "uri=(.*?), method=(.*?), clientIp=(.*?), userAgent=(.*?), " +
                    "level=(.*?), logger=(.*?), thread=(.*?), message=(.*)"
    );

    // line 로그 한 줄을 LogEntry 객체로 파싱
    public LogEntry parse(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            try {
                String timestampStr = matcher.group(1).trim();
                LocalDateTime timestamp = OffsetDateTime.parse(timestampStr).toLocalDateTime();

                return new LogEntry(
                        timestamp,
                        matcher.group(9).trim(),  // level
                        matcher.group(2).trim(),  // traceId
                        matcher.group(3).trim(),  // spanId
                        matcher.group(4).trim(),  // userId
                        matcher.group(5).trim(),  // uri
                        matcher.group(6).trim(),  // method
                        matcher.group(7).trim(),  // clientIp
                        matcher.group(8).trim(),  // userAgent
                        matcher.group(12).trim()  // message
                );
            } catch (DateTimeParseException e) {
                log.warn("⚠️ 날짜 파싱 실패 - timestamp={}", matcher.group(1), e);
                return null;
            }
        }
        return null; // 패턴 불일치 시
    }
}

package com.nana.logsentry.util.parser;


import com.nana.logsentry.model.LogEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParserUtils {

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "timestamp=(.*?), traceId=(.*?), userId=(.*?),\\s*" +
                    "uri=(.*?), method=(.*?), clientIp=(.*?), userAgent=(.*?), " +
                    "level=(.*?), logger=(.*?), thread=(.*?), message=(.*)"
    );

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // line 로그 한 줄을 LogEntry 객체로 파싱
    public LogEntry parse(String line) {
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
        return null; // 파싱 실패 시 null
    }
}
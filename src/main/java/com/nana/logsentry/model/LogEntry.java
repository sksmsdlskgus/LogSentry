package com.nana.logsentry.model;

import java.time.LocalDateTime;

public class LogEntry {

    private final LocalDateTime timestamp;
    private final String level;
    private final String traceId;
    private final String spanId;
    private final String userId;
    private final String uri;
    private final String method;
    private final String clientIp;
    private final String userAgent;
    private final String message;


    public LogEntry(LocalDateTime timestamp, String level, String traceId, String spanId, String userId,
                    String uri, String method, String clientIp, String userAgent, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.traceId = traceId;
        this.spanId = spanId;
        this.userId = userId;
        this.uri = uri;
        this.method = method;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.message = message;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getLevel() { return level; }
    public String getTraceId() { return traceId; }
    public String getSpanId() { return spanId; }
    public String getUserId() { return userId; }
    public String getUri() { return uri; }
    public String getMethod() { return method; }
    public String getClientIp() { return clientIp; }
    public String getUserAgent() { return userAgent; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return String.format(
                "LogEntry[timestamp=%s, level=%s, traceId=%s, spanId=%s, userId=%s, uri=%s, method=%s, clientIp=%s, userAgent=%s, message=%s]",
                timestamp, level, traceId, spanId, userId, uri, method, clientIp, userAgent, message
        );
    }

}

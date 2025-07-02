package com.nana.logsentry.log;

/**
 * MDC(Logback Mapped Diagnostic Context)에서 사용하는 키 값을 모아둔 상수 클래스.
 * 로그 컨텍스트를 일관되게 구성하기 위해 사용.
 */
public class MdcKeys {

    // 고유 추적 정보
    public static final String TRACE_ID = "traceId";       // 분산 추적용 전체 요청 ID (요청 간 전파되는 고유 식별자)
    public static final String SPAN_ID = "spanId";         // 요청 내 개별 작업 단위 식별자 (traceId 하위 작업 단위)
    public static final String TIMESTAMP = "timestamp";    // 요청 시간 (ISO 8601 문자열)

    // 사용자 관련 정보
    public static final String USER_ID = "userId";         // 사용자 ID ("anonymous" 또는 인증된 ID)

    // 요청 관련 정보
    public static final String URI = "uri";                // 요청 경로 (URI)
    public static final String METHOD = "method";          // HTTP 메서드 (GET, POST 등)
    public static final String CLIENT_IP = "clientIp";     // 클라이언트 IP 주소
    public static final String USER_AGENT = "userAgent";   // User-Agent 헤더 정보 (특정 기기에서만 발생하는 이슈 파악을 위해)
}

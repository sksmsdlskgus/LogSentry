package com.nana.logsentry.kafka;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * 공통 Marker 상수 관리
 * - 여러 종류의 Marker를 쓰게 될 경우 중앙에서 관리
 */
public final class KafkaMarker {

    private KafkaMarker() {} // 인스턴스화 방지

    public static final Marker KAFKA = MarkerFactory.getMarker("KAFKA"); // 비즈니스 이벤트, 외부 연동 요청 로그
    public static final Marker ALERT = MarkerFactory.getMarker("ALERT"); // 알림/경보성 로그
    public static final Marker BIZ = MarkerFactory.getMarker("BIZ"); // 비즈니스 로직 트래킹용
}

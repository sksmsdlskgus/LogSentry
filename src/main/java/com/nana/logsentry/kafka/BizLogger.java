package com.nana.logsentry.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka 전송 전용 비즈니스 로거
 * - MarkerFilter와 연동
 * - 유지보수를 위해 모든 Kafka 로그는 이 클래스로 통일
 */
public class BizLogger {

    private static final Logger logger = LoggerFactory.getLogger("kafkaLogger");

    public static void info(String msg, Object... args) {
        logger.info(KafkaMarker.KAFKA, msg, args);
    }

    public static void warn(String msg, Object... args) {
        logger.warn(KafkaMarker.KAFKA, msg, args);
    }

    public static void error(String msg, Object... args) {
        logger.error(KafkaMarker.KAFKA, msg, args);
    }
}

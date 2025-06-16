package com.nana.logsentry.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class LogHeartbeatScheduler {

    private static final Logger heartbeatLogger = LoggerFactory.getLogger("heartbeatLogger");

    @Scheduled(cron = "0 */5 * * * *") // 매 5분
    // @Scheduled(cron = "*/30 * * * * *") // 테스트용 30초
    public void heartbeatLog() {
        heartbeatLogger.debug("[HEARTBEAT] 💓 App alive at {}", LocalDateTime.now());
    }
}

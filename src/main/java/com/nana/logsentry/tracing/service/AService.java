package com.nana.logsentry.tracing.service;

import com.nana.logsentry.tracing.client.BServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AService { // 비즈니스 로직 및 클라이언트 호출

    private static final Logger log = LoggerFactory.getLogger(AService.class);

    private final BServiceClient bServiceClient;

    public String doBusiness() {
        log.info("[AService] B 서비스 호출 시작");
        String response = bServiceClient.callB();
        log.info("[AService] B 서비스 응답 수신: {}", response);
        return "A 완료 + " + response;
    }
}

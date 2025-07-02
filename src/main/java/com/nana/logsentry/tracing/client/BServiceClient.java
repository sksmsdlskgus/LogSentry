package com.nana.logsentry.tracing.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BServiceClient { // RestTemplate로 B 서비스 호출

    private final RestTemplate restTemplate;

    public String callB() {
        String url = "http://localhost:8081/b/hello";
        return restTemplate.getForObject(url, String.class);
    }
}

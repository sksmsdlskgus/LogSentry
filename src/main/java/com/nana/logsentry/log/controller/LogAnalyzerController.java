package com.nana.logsentry.log.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogAnalyzerController {

    // 최신 로그 조회 API -> GET /api/logs/latest?limit=10
    // TOP5 요청 IP 조회 API -> GET /api/logs/top/ip
    // TOP5 요청 URL 조회 API -> GET /api/logs/top/uri
    // 로그 파일 날짜 목록 조회 -> GET /api/logs/dates


}

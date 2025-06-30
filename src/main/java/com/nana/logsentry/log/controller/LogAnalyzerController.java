package com.nana.logsentry.log.controller;

import com.nana.logsentry.log.service.LogAnalyzerService;
import com.nana.logsentry.model.LogEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "로그 통계", description = "로그 파일 분석 및 통계 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogAnalyzerController {

    private final LogAnalyzerService logAnalyzerService;

    @Operation(summary = "최신 로그 조회",
            description = "지정한 날짜의 로그 파일에서 최대 100개의 최신 요청 로그를 조회합니다.",
            responses = { @ApiResponse(responseCode = "200", description = "정상적으로 로그를 조회했습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 로그를 조회하지 못했습니다.")})
    @GetMapping("/latest")
    public List<LogEntry> getLatestLogs(@RequestParam(name = "date") String date) {
        return logAnalyzerService.getLatestLogs(date);
    }




}

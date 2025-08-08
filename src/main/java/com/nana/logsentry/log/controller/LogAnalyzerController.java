package com.nana.logsentry.log.controller;

import com.nana.logsentry.log.dto.response.LogEntryResponseDto;
import com.nana.logsentry.log.dto.request.LogFilterRequestDto;
import com.nana.logsentry.log.dto.response.TopIpStatResponseDto;
import com.nana.logsentry.log.dto.response.TopUriStatResponseDto;
import com.nana.logsentry.log.service.LogAnalyzerService;
import com.nana.logsentry.model.LogEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @Operation(summary = "TOP5 IP 통계",
            description = "기준 날짜로부터 30일 간 가장 많은 요청을 발생시킨 IP TOP5를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "IP 통계를 정상적으로 조회했습니다."),
                    @ApiResponse(responseCode = "500", description = "통계 조회 중 서버 오류가 발생했습니다.")}
    )
    @GetMapping("/top/ip")
    public List<TopIpStatResponseDto> getTopIpStats(@RequestParam(name = "baseDate")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate baseDate){
        return logAnalyzerService.getTopLogIp(baseDate);
    }

    @Operation(summary = "TOP5 URI 통계",
            description = "기준 날짜로부터 30일 간 가장 많이 호출된 URI TOP5를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "URI 통계를 정상적으로 조회했습니다."),
                    @ApiResponse(responseCode = "500", description = "통계 조회 중 서버 오류가 발생했습니다.")}
    )
    @GetMapping("/top/uri")
    public List<TopUriStatResponseDto> getTopUriStats(@RequestParam(name = "baseDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate) {
        return logAnalyzerService.getTopLogUri(baseDate);
    }

    @Operation(summary = "로그 파일 날짜 목록 조회",
            description = "현재 저장된 로그 파일들의 날짜 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "날짜 목록을 정상적으로 조회했습니다."),
                    @ApiResponse(responseCode = "500", description = "조회 중 오류가 발생했습니다.")}
    )
    @GetMapping("/dates")
    public List<String> getLogFileDates() {
        return logAnalyzerService.getLogFileDate();
    }

    @Operation(summary = "로그 필터 조회",
            description = "다양한 조건(IP, URI, Method, 상태코드, 레벨, traceId 등)으로 로그를 필터링합니다. 미입력 시 최신순 100개 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "필터링된 로그 목록을 반환했습니다."),
                    @ApiResponse(responseCode = "500", description = "로그 필터링 중 서버 오류가 발생했습니다.")}
    )
    @GetMapping("/filter")
    public List<LogEntryResponseDto> filterLogs(@Validated @ModelAttribute LogFilterRequestDto request) {
        if (request.getStartDate() == null) {
            request.setStartDate(LocalDate.now().minusDays(7));
        }
        if (request.getEndDate() == null) {
            request.setEndDate(LocalDate.now());
        }

        return logAnalyzerService.filterLogs(request)
                .stream()
                .map(LogEntryResponseDto::from)
                .toList();
    }

}

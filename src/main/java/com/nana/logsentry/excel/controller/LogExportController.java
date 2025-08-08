package com.nana.logsentry.excel.controller;


import com.nana.logsentry.excel.service.LogExportService;
import com.nana.logsentry.model.LogEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "로그 분석 및 내보내기", description = "로그 파일을 분석하고 Excel로 내보내는 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogExportController {

    private final LogExportService logExportService;

    @Operation(summary = "로그 분석", description = "지정된 날짜의 로그 파일을 분석하여 요청 수, IP/URI/Method별 요청 통계를 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "분석 결과 반환"),
            @ApiResponse(responseCode = "404", description = "해당 날짜의 로그 파일이 존재하지 않음")
    })
    @GetMapping("/analyze/{date}")
    public Map<String,Object> analyzeLog(@PathVariable(name = "date") String date){
        List<LogEntry> entries = logExportService.parseLogFile(date);

        Map<String,Object> analysis = new HashMap<>();
        if (entries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, date + " 날짜의 로그 데이터가 없습니다.");
        } else {
            analysis.put("totalRequests", entries.size());
            analysis.put("requestsByIp", logExportService.getRequestsByIp(entries));
            analysis.put("requestsByUri", logExportService.getRequestsByUri(entries));
            analysis.put("requestsByMethod", logExportService.getRequestsByMethod(entries));

        }

        return analysis;
    }

    @Operation(summary = "Excel 로그 다운로드", description = "지정된 날짜의 로그 데이터를 Excel(.xlsx) 파일로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
            @ApiResponse(responseCode = "500", description = "파일 생성 또는 다운로드 실패")
    })
    @GetMapping("/download/{date}")
    public ResponseEntity<Resource> downloadExcel(@PathVariable(name = "date") String date) {
        try {
            List<LogEntry> entries = logExportService.parseLogFile(date);

            // 디렉토리 생성 (없으면 생성)
            Path directory = Paths.get("logs/excel");
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            logExportService.exportToExcel(entries, date);

            Path file = Paths.get("logs/excel/client-requests-" + date + ".xlsx");
            Resource resource = new UrlResource(file.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"client-requests-" + date + ".xlsx\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 파일 경로", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류 발생", e);
        }
    }


}

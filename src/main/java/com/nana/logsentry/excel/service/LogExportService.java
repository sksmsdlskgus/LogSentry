package com.nana.logsentry.excel.service;

import com.nana.logsentry.util.parser.LogParserUtils;
import com.nana.logsentry.model.LogEntry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogExportService {

    private final LogParserUtils parser = new LogParserUtils();

    // 지정된 날짜의 로그 파일을 파싱하여 LogEntry 리스트로 반환
    public List<LogEntry> parseLogFile(String date) {
        String logFile = String.format("logs/text/client-requests.%s.log", date);
        Path logPath = Paths.get(logFile);

        if (!Files.exists(logPath)) {
            return Collections.emptyList(); // 해당 날짜의 로그 파일이 없으면 빈 리스트 반환
        }

        try {
            return Files.lines(logPath)
                    .map(parser::parse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("로그 파일 읽기 실패: " + logFile, e);
        }
    }

    // 어떤 IP가 얼마나 요청했는지
    public Map<String, Long> getRequestsByIp(List<LogEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getClientIp, Collectors.counting()));
    }

    // 어떤 API가 많이 호출됐는지
    public Map<String, Long> getRequestsByUri(List<LogEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getUri, Collectors.counting()));
    }

    // GET/POST 분포 확인
    public Map<String, Long> getRequestsByMethod(List<LogEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getMethod, Collectors.counting()));
    }

    // 로그 항목 리스트를 기반으로 요청 상세 시트 및 통계 시트를 포함한 Excel 파일을 생성
    public void exportToExcel(List<LogEntry> entries, String date) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // 요청 상세 시트 생성
            Sheet detailSheet = workbook.createSheet("요청 상세");

            // 헤더 작성
            Row headerRow = detailSheet.createRow(0);
            headerRow.createCell(0).setCellValue("시간");
            headerRow.createCell(1).setCellValue("Level");
            headerRow.createCell(2).setCellValue("Trace ID");
            headerRow.createCell(3).setCellValue("Span ID");
            headerRow.createCell(4).setCellValue("User ID");
            headerRow.createCell(5).setCellValue("URI");
            headerRow.createCell(6).setCellValue("Method");
            headerRow.createCell(7).setCellValue("IP");
            headerRow.createCell(8).setCellValue("User-Agent");
            headerRow.createCell(9).setCellValue("Message");


            // 데이터 행 추가
            int rowNum = 1;
            for (LogEntry entry : entries) {
                Row row = detailSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                row.createCell(1).setCellValue(entry.getLevel());
                row.createCell(2).setCellValue(entry.getTraceId());
                row.createCell(3).setCellValue(entry.getSpanId());
                row.createCell(4).setCellValue(entry.getUserId());
                row.createCell(5).setCellValue(entry.getUri());
                row.createCell(6).setCellValue(entry.getMethod());
                row.createCell(7).setCellValue(entry.getClientIp());
                row.createCell(8).setCellValue(entry.getUserAgent());
                row.createCell(9).setCellValue(entry.getMessage());
            }

            // 통계 시트 생성
            Sheet statsSheet = workbook.createSheet("통계");

            // 각 통계 섹션 삽입
            createStatisticsSection(statsSheet, 0, "IP별 요청 수", getRequestsByIp(entries));
            createStatisticsSection(statsSheet, getRequestsByIp(entries).size() + 2,
                    "URI별 요청 수", getRequestsByUri(entries));
            createStatisticsSection(statsSheet,
                    getRequestsByIp(entries).size() + getRequestsByUri(entries).size() + 4,
                    "메소드별 요청 수", getRequestsByMethod(entries));

            // 자동 컬럼 크기 조정
            for (int i = 0; i < 10; i++) {
                detailSheet.autoSizeColumn(i);
                statsSheet.autoSizeColumn(i);
            }

            // 파일로 저장
            String fileName = String.format("logs/excel/client-requests-%s.xlsx", date);
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성 실패", e);
        }
    }

    // 대상 시트, 시작 행 번호, 섹션 제목, 통계 데이터 맵 삽입 <통계 데이터>
    private void createStatisticsSection(Sheet sheet, int startRow, String title,
                                         Map<String, Long> data) {
        Row titleRow = sheet.createRow(startRow);
        titleRow.createCell(0).setCellValue(title);

        Row headerRow = sheet.createRow(startRow + 1);
        headerRow.createCell(0).setCellValue("구분");
        headerRow.createCell(1).setCellValue("요청 수");

        int rowNum = startRow + 2;
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
    }



}
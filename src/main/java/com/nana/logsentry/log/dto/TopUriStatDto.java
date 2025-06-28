package com.nana.logsentry.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopUriStatDto {
    // 가장 많이 요청한 URL 5개 Dto
    private String uri;
    private int count;
}
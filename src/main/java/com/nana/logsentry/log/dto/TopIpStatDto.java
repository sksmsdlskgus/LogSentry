package com.nana.logsentry.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopIpStatDto {
    // 가장 많이 요청한 IP 5개 Dto
    private String clientIp;
    private int count;

}

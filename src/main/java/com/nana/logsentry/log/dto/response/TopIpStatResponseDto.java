package com.nana.logsentry.log.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopIpStatResponseDto {

    @Schema(description = "클라이언트 IP 주소", example = "192.168.0.1")
    private String clientIp;

    @Schema(description = "해당 IP의 요청 횟수", example = "123")
    private int count;

}

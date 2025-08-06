package com.nana.logsentry.log.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopUriStatResponseDto {

    @Schema(description = "요청 URI", example = "/api/data")
    private String uri;

    @Schema(description = "해당 URI의 요청 횟수", example = "87")
    private int count;
}
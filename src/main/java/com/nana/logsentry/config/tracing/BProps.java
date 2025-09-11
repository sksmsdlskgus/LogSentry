package com.nana.logsentry.config.tracing;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "b")
public record BProps(String baseUrl) { }
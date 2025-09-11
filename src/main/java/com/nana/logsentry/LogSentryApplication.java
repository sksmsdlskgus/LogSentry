package com.nana.logsentry;

import com.nana.logsentry.config.tracing.BProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(BProps.class)
@EnableScheduling //스케줄링 활성화
public class LogSentryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogSentryApplication.class, args);
	}

}

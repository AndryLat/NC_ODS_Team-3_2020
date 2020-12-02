package com.netcracker.odstc.logviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RealtimeLogViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeLogViewerApplication.class, args);
	}
}

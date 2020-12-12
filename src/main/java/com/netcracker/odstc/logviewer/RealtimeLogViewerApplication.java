package com.netcracker.odstc.logviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RealtimeLogViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeLogViewerApplication.class, args);
	}

}

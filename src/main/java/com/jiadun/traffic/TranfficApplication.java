package com.jiadun.traffic;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties
@SpringBootApplication
@EnableScheduling
public class TranfficApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(TranfficApplication.class, args);
		
	}
}

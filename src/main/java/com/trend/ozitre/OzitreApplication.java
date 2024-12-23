package com.trend.ozitre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

@SpringBootApplication(exclude = {QuartzAutoConfiguration.class})
public class OzitreApplication {

	public static void main(String[] args) {
		SpringApplication.run(OzitreApplication.class, args);
	}

}

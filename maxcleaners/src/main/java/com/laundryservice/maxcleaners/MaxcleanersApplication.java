package com.laundryservice.maxcleaners;

import com.laundryservice.maxcleaners.constant.MaxcleanerConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MaxcleanersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaxcleanersApplication.class, args);
		System.out.println("URL::"+ MaxcleanerConstants.GEO_BASEURL);
		System.out.println("stores::"+MaxcleanerConstants.stores);
		System.out.println("MILES::"+MaxcleanerConstants.MILES);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

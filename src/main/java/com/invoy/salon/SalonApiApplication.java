package com.growthhub.salon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SalonApiApplication {
    private final RestTemplate restTemplate = new RestTemplate();
    public static void main(String[] args) {
        SpringApplication.run(SalonApiApplication.class, args);
    }
    
    @Scheduled(fixedRate = 240000) // 4 minutes
    public void pingUrl() {
        try {
            String url = "https://growthhubbe.onrender.com/swagger-ui/index.html";
            restTemplate.getForObject(url, String.class);
            System.out.println("Ping success");
        } catch (Exception e) {
            System.out.println("Ping failed: " + e.getMessage());
        }
    }
}

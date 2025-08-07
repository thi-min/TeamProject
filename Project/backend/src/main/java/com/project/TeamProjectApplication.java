package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling	//스케쥴러때문에 추가
public class TeamProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeamProjectApplication.class, args);
    }
}

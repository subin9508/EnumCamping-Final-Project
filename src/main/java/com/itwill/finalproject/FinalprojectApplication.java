package com.itwill.finalproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class FinalprojectApplication { // 깃 경로 수정 테스트

	public static void main(String[] args) {
		SpringApplication.run(FinalprojectApplication.class, args);
	}

}

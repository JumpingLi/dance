package com.champion.dance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@ServletComponentScan(basePackages = "com.champion.dance.filter")
@ComponentScan(basePackages = "com.champion")
@MapperScan(basePackages = "com.champion.dance.domain.mapper")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DanceApplication.class, args);
	}
}

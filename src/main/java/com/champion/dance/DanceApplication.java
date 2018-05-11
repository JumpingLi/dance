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
	private static Thread mainThread = Thread.currentThread();
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            mainThread.interrupt();
            while (true) {
                try {
                    mainThread.join();
                    break;
                } catch (InterruptedException e) {
                    System.out.println("程序正在结束中，请勿强制结束程序！！！");
                }
            }
            System.out.println("程序正常结束");
        }));
		SpringApplication.run(DanceApplication.class, args);
	}
}

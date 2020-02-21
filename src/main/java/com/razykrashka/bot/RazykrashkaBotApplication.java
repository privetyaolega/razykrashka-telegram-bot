package com.razykrashka.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableJpaRepositories
public class RazykrashkaBotApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(RazykrashkaBotApplication.class, args);
    }
}
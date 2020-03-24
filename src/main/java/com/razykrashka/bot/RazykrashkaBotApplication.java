package com.razykrashka.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableJpaRepositories
public class RazykrashkaBotApplication extends SpringBootServletInitializer {

    {
        ApiContextInitializer.init();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RazykrashkaBotApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RazykrashkaBotApplication.class, args);
    }
}
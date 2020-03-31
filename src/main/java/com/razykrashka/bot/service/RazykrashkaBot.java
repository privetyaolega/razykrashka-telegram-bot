package com.razykrashka.bot.service;

import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = "classpath:/props/razykrashka.yaml", factory = YamlPropertyLoaderFactory.class)
public class RazykrashkaBot extends TelegramLongPollingBot {

    @Value("${razykrashka.bot.username}")
    String botUsername;
    @Value("${razykrashka.bot.token}")
    String botToken;

    @Autowired
    ApplicationContext context;
    @Autowired
    BotExecutor botExecutor;
    Update realUpdate;

    @Override
    public void onUpdateReceived(Update update) {
        this.realUpdate = update;
        botExecutor.execute(update);
    }
}
package com.razykrashka.bot.job;

import com.razykrashka.bot.db.service.MeetingService;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PROTECTED)
@PropertySource(value = {"classpath:/props/job.yaml", "classpath:/props/razykrashka.yaml"}, factory = YamlPropertyLoaderFactory.class)
@EnableScheduling
public class AbstractJob {
    @Value("${job.enabled}")
    boolean jobEnabled;
    @Value("${razykrashka.group.id}")
    String groupChatId;
    @Autowired
    MeetingService meetingService;
    @Autowired
    MessageManager messageManager;
    @Autowired
    KeyboardBuilder keyboardBuilder;
}
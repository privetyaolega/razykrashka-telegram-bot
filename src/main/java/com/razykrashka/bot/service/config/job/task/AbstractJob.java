package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.db.service.MeetingService;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PROTECTED)
@PropertySource(value = {"classpath:/props/razykrashka.yaml"}, factory = YamlPropertyLoaderFactory.class)
public abstract class AbstractJob {
    @Value("${razykrashka.group.id}")
    String groupChatId;
    @Autowired
    MeetingService meetingService;
    @Autowired
    MessageManager messageManager;
    @Autowired
    KeyboardBuilder keyboardBuilder;
}
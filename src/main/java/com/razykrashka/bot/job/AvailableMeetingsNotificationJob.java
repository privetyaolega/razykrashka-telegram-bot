package com.razykrashka.bot.job;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.service.MeetingService;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.stage.meeting.view.all.ActiveMeetingsViewStage;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = {"classpath:/props/job.yaml", "classpath:/props/razykrashka.yaml"},
        factory = YamlPropertyLoaderFactory.class)
public class AvailableMeetingsNotificationJob extends AbstractJob{

    @Value("${job.enabled}")
    boolean jobEnabled;

    @Scheduled(fixedRateString = "${job.rate}")
    public void availableMeetingsNotificationJob() {
        if (jobEnabled) {
            log.info("JOB: Available meeting notification job is started.");
            List<Meeting> availableMeetings = meetingService.getAllCreationStatusDone();
            String message;
            InlineKeyboardMarkup keyboard = null;
            if (availableMeetings.isEmpty()) {
                message = "There is no any available meeting. Work hard and create meeting!";
            } else {
                message = "Hey, guys! \uD83D\uDC4B\n" +
                        "YOOOHOOOO! There are " + availableMeetings.size() + " available meetings!\n" +
                        "Hurry up and join! Practice makes perfect. \uD83D\uDCAA";
                keyboard = keyboardBuilder.getKeyboard()
                        .setRow("Show available meetings ✨", ActiveMeetingsViewStage.class.getSimpleName() + "fromGroup")
                        .build();
            }
            messageManager.sendMessage(new SendMessage()
                    .setParseMode(ParseMode.HTML)
                    .setChatId(groupChatId)
                    .setText(message)
                    .setReplyMarkup(keyboard)
                    .disableWebPagePreview());
        }
    }
}
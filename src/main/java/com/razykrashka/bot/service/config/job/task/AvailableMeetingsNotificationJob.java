package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.job.AbstractJob;
import com.razykrashka.bot.stage.meeting.view.all.ActiveMeetingsViewStage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static com.razykrashka.bot.ui.helpers.UpdateHelper.FROM_GROUP;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableMeetingsNotificationJob extends AbstractJob implements Runnable {

    final static String MAIN_MESSAGE = "Hey, guys! " + Emoji.WAVE_HAND + "\n" +
            "Yahooooo! There are %s available meetings!\n" +
            "Hurry up and join! Practice makes perfect. " + Emoji.BICEPS;
    final static String NO_MEETINGS_MESSAGE = "There is no any available meeting. Work hard and create meeting!";

    /**
     *
     * Job notifies about available(vacant) meetings in main group chat.
     * Message consist of:
     * - message with amount of meetings and some common information
     * - inline button that is trigger to AllMeetingViewStage for calling user.
     * The message is sent to calling user directly (user chat);
     *
     * Period: every day;
     *
     */

    public void run() {
        log.info("JOB: Available meeting notification job is started.");
        List<Meeting> availableMeetings = meetingService.getAllCreationStatusDone();
        String message;
        if (!availableMeetings.isEmpty()) {
            message = String.format(MAIN_MESSAGE, availableMeetings.size());
            InlineKeyboardMarkup keyboard = keyboardBuilder
                    .getKeyboard()
                    .setRow("Show available meetings ✨",
                            ActiveMeetingsViewStage.class.getSimpleName() + FROM_GROUP)
                    .build();

            messageManager.disableKeyboardLastBotMessage(groupChatId)
                    .sendMessage(new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(groupChatId)
                            .setText(message)
                            .setReplyMarkup(keyboard));
        }
    }
}

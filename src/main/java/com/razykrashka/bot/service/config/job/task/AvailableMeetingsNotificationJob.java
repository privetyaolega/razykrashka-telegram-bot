package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.job.AbstractJob;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableMeetingsNotificationJob extends AbstractJob implements Runnable {

    final static String NO_MEETINGS_MESSAGE = "There is no any available meeting. Work hard and create meeting!";

    List<String> messages;

    public AvailableMeetingsNotificationJob() {
        messages = Arrays.asList(
                "Hey, guys! " + Emoji.WAVE_HAND + "\nYahooooo! There are %s available meetings!\nHurry up and join! Practice makes perfect " + Emoji.BICEPS,
                "Hey! How are you? We miss you" + Emoji.DISAPPOINTED_RELIEVED + "\nWould you like to join a meeting soon?",
                "Well, hello there! We’ve been told your English needs some practice. Let’s join a meeting from the list!",
                "Hiii! What's up? Wanna practice some English later?",
                "Are you dreaming of speaking perfect English? Well, you have to start somewhere. Join a meeting and start improving your skills now!",
                "Stop being someone who learns English and become someone who speaks it! Join a meeting now " + Emoji.HUG
        );
    }

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
            message = String.format(getRandomMessage(), availableMeetings.size());
            InlineKeyboardMarkup keyboard = keyboardBuilder
                    .getKeyboard()
                    .setRow(new InlineKeyboardButton()
                            .setText("Show available meetings ✨")
                            .setUrl("https://t.me/RazykrashkaBot"))
                    .build();

            messageManager.sendMessage(new SendMessage()
                    .setParseMode(ParseMode.HTML)
                    .setChatId(groupChatId)
                    .setText(message)
                    .setReplyMarkup(keyboard));
        }
    }

    private String getRandomMessage() {
        return messages.get(new Random().nextInt(messages.size()));
    }
}

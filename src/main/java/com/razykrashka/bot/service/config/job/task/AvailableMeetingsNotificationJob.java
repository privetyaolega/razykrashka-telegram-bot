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
                "Well, hello there!\nWe’ve been told your English needs some practice. Let’s join a meeting from the list!",
                "Hiii! What's up?\nWanna practice some English later?",
                "Are you dreaming of speaking perfect English? Well, you have to start somewhere.\nJoin a meeting and start improving your skills now!",
                "Stop being someone who learns English and become someone who speaks it!\nJoin a meeting now " + Emoji.HUG,
                "Heeeeyyy! \uD83D\uDE1C\n\nWhile you were away, some new meetings appeared. \nDon’t hesitate and join one of them right now!",
                "Well well well... What was the last time you practiced your English?\nCheck the new meetings out! \uD83D\uDE08",
                "Hi there! Here are some new meetings you can join.\nDon’t give up on your dream of being fluent! " + Emoji.BICEPS,
                "Procrastinating on your English practice again? You’re not alone here \uD83D\uDE11\nLet’s fight procrastination together! \uD83D\uDC4A\uD83C\uDFFB\n\nJoin one of the available meetings",
                "Hi there! Look how many new meetings are waiting for you to join \uD83D\uDE0C"
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
//                + "\n⚠️\n" +
//                "Sorry, but our bot is still in the test mode\n" +
//                "None of the meetings are valid yet.\n" +
//                "We want to make it better for you and perfection takes time!\n" +
//                "⚠️";
    }
}

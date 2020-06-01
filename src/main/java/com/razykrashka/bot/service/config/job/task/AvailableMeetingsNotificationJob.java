package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.constants.Emoji;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableMeetingsNotificationJob extends AbstractJob implements Runnable {

    final static String NO_MEETINGS_MESSAGE = "There is no any available meeting. Work hard and create meeting!";
    Map<String, String> messages;

    public AvailableMeetingsNotificationJob() {
        messages = new HashMap<>();
        messages.put("Hey, guys! " + Emoji.WAVE_HAND + "\nYahooooo! There are %s available meetings!\nHurry up and join! Practice makes perfect " + Emoji.BICEPS, "Let's go! " + Emoji.BICEPS);
        messages.put("Hey! How are you? We miss you" + Emoji.DISAPPOINTED_RELIEVED + "\nWould you like to join a meeting soon?", "Show available meetings ✨");
        messages.put("Well, hello there!\nWe’ve been told your English needs some practice. Let’s join a meeting from the list!", "Show available meetings ✨");
        messages.put("Hiii! What's up?\nWanna practice some English later?", "Show available meetings ✨");
        messages.put("Are you dreaming of speaking perfect English? Well, you have to start somewhere.\n\nJoin a meeting and start improving your skills now!", "Show available meetings ✨");
        messages.put("Stop being someone who learns English and become someone who speaks it!\n\nJoin a meeting now " + Emoji.HUG, "Show available meetings ✨");
        messages.put("Heeeeyyy! \uD83D\uDE1C\n\nWhile you were away, some new meetings appeared. \nDon’t hesitate and join one of them right now!", "Show available meetings ✨");
        messages.put("Well well well... What was the last time you practiced your English?\nCheck the new meetings out! \uD83D\uDE08", "Show available meetings ✨");
        messages.put("Hi there! Here are some new meetings you can join.\nDon’t give up on your dream of being fluent! " + Emoji.BICEPS, "Show available meetings ✨");
        messages.put("Procrastinating on your English practice again? You’re not alone here \uD83D\uDE11\nLet’s fight procrastination together! \uD83D\uDC4A\uD83C\uDFFB\n\nJoin one of the available meetings", "Show available meetings ✨");
        messages.put("Hi there! Look how many new meetings are waiting for you to join \uD83D\uDE0C", "Show available meetings ✨");
        messages.put("Hey! \uD83D\uDC4B\uD83C\uDFFB\nI think you're missing something important in your life. Like very very important... We mean, of course, practicing English! \uD83D\uDE05\n\nCreating a meeting will most probably fix it \uD83D\uDE09 \nLet’s try. It will only take you a few clicks!", "Let's practice \uD83D\uDC4A\uD83C\uDFFB");
        messages.put("Speaking English is no easy-peasy… But laying on a sofa and watching TV-shows is \uD83D\uDCFA\n\nWill it take you where you want to be though? \nStart making effort and your life will change! Create a meeting now \uD83D\uDE09", "Create meeting " + Emoji.BICEPS);
        messages.put("The English language says:\n“Seems like you’re always too busy for me. \nOk, then I won’t help you when you most need me! \uD83D\uDE12”\n\nDon’t let this happen and start practicing now!  ⬇️", "Start practice right now! " + Emoji.BICEPS);
        messages.put("Make your mom proud and start speaking English now!\nPress ‘Create a Meeting’. We will check! \uD83D\uDE09", "Create meeting! \uD83E\uDD24");
    }

    /**
     *
     * Job notifies about available(vacant) meetings in main group chat.
     * Message consist of:
     * - message with amount of meetings and some common information
     * - inline button that is trigger to AllMeetingViewStage for calling user.
     * The message is sent to calling user directly (user chat);
     *
     */

    public void run() {
        log.info("JOB: Available meeting notification job is started...");
        String message;
        Pair<String, String> randomMessage = getRandomMessage();
        message = String.format(randomMessage.getFirst(), messages.size());
        InlineKeyboardMarkup keyboard = keyboardBuilder
                .getKeyboard()
                .setRow(new InlineKeyboardButton()
                        .setText(randomMessage.getSecond())
                        .setUrl("https://t.me/RazykrashkaBot"))
                .build();

        messageManager.sendMessage(new SendMessage()
                .setParseMode(ParseMode.HTML)
                .setChatId(groupChatId)
                .setText(message)
                .setReplyMarkup(keyboard));
    }

    private Pair<String, String> getRandomMessage() {
        String key = new ArrayList<>(messages.keySet())
                .get(new Random().nextInt(messages.size()));
        return Pair.of(key, messages.get(key));
    }
}
package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.job.AbstractJob;
import com.razykrashka.bot.stage.meeting.view.single.SingleMeetingViewMainStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpcomingMeetingsNotificationJob extends AbstractJob implements Runnable {

    List<String> messages;

    public UpcomingMeetingsNotificationJob() {
        messages = Arrays.asList(
                "Wakey wakey, rise and shine! ☀️\nYou have a meeting today at %s\nDon’t miss it!",
                "Hey! Do you remember about today’s meeting at %s?\nWish you a great time!",
                "Good morning! \uD83E\uDD17 \nDon't forget you have a very important meeting today at %s",
                "GM! We hope you slept well today because we need you fresh and happy at %s",
                "Morning! " + Emoji.SUNNY + "\nWhat a wonderful day to practice your English!\nSee you at %s",
                "Tell everyone you’re busy today because you have an urgent meeting at %s",
                "Hey! " + Emoji.WAVE_HAND + "\nJust a quick reminder that you have a meeting today at %s.\nHave fun! " + Emoji.WINK,
                "Good morning!\nAre you rested and ready for the meeting?! " + Emoji.BICEPS + "\nSee you at %s"
        );
    }

    /**
     *
     * Job notifies all meeting participants by sending reminder that
     * contains link for meeting with brief information about it.
     * Each meeting member receives the message in user chat (including meeting owner)
     *
     * Period: morning of every day.
     *
     */
    @Override
    public void run() {
        log.info("JOB: Upcoming meeting notification job is started.");
        List<Meeting> availableMeetings = meetingService.getAllMeetingDateToday();

        String meetingTodayString = availableMeetings.isEmpty() ? "NO MEETING" :
                availableMeetings.stream()
                        .map(meeting -> String.valueOf(meeting.getId()))
                        .collect(Collectors.joining(","));
        log.info("JOB: Upcoming meetings on {} -> {}", LocalDate.now().toString(), meetingTodayString);

        availableMeetings.forEach(m -> {
            String participantsToString = m.getParticipants().stream()
                    .map(this::getUserString)
                    .collect(Collectors.joining(","));

            log.info("JOB: Meeting {} has the following participants: [{}]", m.getId(), participantsToString);

            m.getParticipants().forEach(p -> {
                String id = String.valueOf(p.getId());
                messageManager
                        .disableKeyboardLastBotMessage(id)
                        .sendRandomSticker("greeting", p.getId())
                        .sendMessage(new SendMessage()
                                .setParseMode(ParseMode.HTML)
                                .setChatId(id)
                                .setText(getMessage(m))
                                .setReplyMarkup(getKeyboard(m)));
            });
        });
    }

    private InlineKeyboardMarkup getKeyboard(Meeting m) {
        return keyboardBuilder
                .getKeyboard()
                .setRow("Open meeting’s details " + Emoji.DIZZY, SingleMeetingViewMainStage.class.getSimpleName() + m.getId())
                .build();
    }

    private String getMessage(Meeting m) {
        String message = messages.get(new Random().nextInt(messages.size()));
        String time = TextFormatter.getBoldString(m.getMeetingDateTime().
                format(DateTimeFormatter.ofPattern("HH:mm")));
        return String.format(message + "\n\n<b>/meeting%s</b>✨", time, m.getId());
    }

    private String getUserString(TelegramUser u) {
        return u.getId() + " " + (Optional.ofNullable(u.getUserName()).isPresent() ? u.getUserName() : "");
    }
}
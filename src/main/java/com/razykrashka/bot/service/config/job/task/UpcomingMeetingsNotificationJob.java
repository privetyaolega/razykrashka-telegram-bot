package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpcomingMeetingsNotificationJob extends AbstractJob implements Runnable {

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
                messageManager.sendMessage(new SendMessage()
                        .setParseMode(ParseMode.HTML)
                        .setChatId(String.valueOf(p.getTelegramId()))
                        .setText("Hey! Do you remember about today’s meeting at " + m.getMeetingDateTime() +
                                "? Wish you a great time! ✨\n/meeting" + m.getId())
                        .disableWebPagePreview());
            });
        });
    }

    private String getUserString(TelegramUser u) {
        return u.getTelegramId() + " " + (Optional.ofNullable(u.getUserName()).isPresent() ? u.getUserName() : "");
    }
}
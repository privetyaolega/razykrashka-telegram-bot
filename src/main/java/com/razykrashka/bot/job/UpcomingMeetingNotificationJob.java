package com.razykrashka.bot.job;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpcomingMeetingNotificationJob extends AbstractJob {

    @Value("${job.meeting.notification.upcoming.enabled}")
    boolean jobEnabled;

    /**
     *
     * Job notifies all meeting participants by sending reminder that
     * contains link for meeting with brief information about it.
     * Each meeting member receives the message in user chat (including meeting owner)
     *
     * Period: morning of every day.
     *
     */
    @Scheduled(fixedRateString = "${job.meeting.notification.upcoming.rate}")
    public void availableMeetingsNotificationJob() {
        if (jobEnabled) {
            log.info("JOB: Upcoming meeting notification job is started.");
            List<Meeting> availableMeetings = meetingService.getAllMeetingDateToday();

            log.info("JOB: Upcoming meetings on {} -> {}", LocalDate.now().toString(),
                    availableMeetings.isEmpty() ? "NO MEETING" : availableMeetings.stream()
                            .map(meeting -> String.valueOf(meeting.getId()))
                            .collect(Collectors.joining(",")));

            availableMeetings.forEach(m -> {
                log.info("JOB: Meeting {} has the following participants:\n{} ", m.getId(), m.getParticipants().stream()
                        .map(telegramUser -> telegramUser.getTelegramId() + " " +
                                (Optional.ofNullable(telegramUser.getUserName()).isPresent() ? telegramUser.getUserName() : ""))
                        .collect(Collectors.joining(",")));

                m.getParticipants().forEach(p -> {
                    messageManager.sendMessage(new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(String.valueOf(p.getTelegramId()))
                            .setText("Hello! Don't forget, that you have meeting today! ✨\n/meeting" + m.getId())
                            .disableWebPagePreview());
                });
            });
        }
    }
}
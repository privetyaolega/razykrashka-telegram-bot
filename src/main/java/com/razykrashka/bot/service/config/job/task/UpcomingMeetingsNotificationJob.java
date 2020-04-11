package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.job.AbstractJob;
import com.razykrashka.bot.service.config.job.properties.JobRunnable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@ToString
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpcomingMeetingsNotificationJob extends AbstractJob implements JobRunnable {

    boolean enabled;
    String cronExp;
    String name;

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
      /*  List<Meeting> availableMeetings = meetingService.getAllMeetingDateToday();

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
                        .setText("Hey! Do you remember about today’s meeting? Wish you a great time! ✨\n/meeting" + m.getId())
                        .disableWebPagePreview());
            });
        });*/
    }
}
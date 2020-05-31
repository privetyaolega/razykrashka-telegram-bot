package com.razykrashka.bot.service.config.job.task;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRightBeforeMeetingJob extends AbstractJob implements Runnable {

    public void run() {
        log.info("JOB: Notification right before meeting job is started...");
        List<Meeting> upcomingMeetings = meetingService.getAllUpcomingMeetings()
//                .filter(m -> LocalDateTime.now().isAfter(m.getMeetingDateTime().minusHours(1)))
                .filter(m -> LocalDateTime.now().plusHours(1).getHour() == m.getMeetingDateTime().getHour())
                .collect(Collectors.toList());



    }
}
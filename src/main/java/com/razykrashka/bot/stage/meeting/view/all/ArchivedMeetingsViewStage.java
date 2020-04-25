package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class ArchivedMeetingsViewStage extends PaginationMeetingsViewStage {

    public static final String KEYWORD = "/archived";

    @Override
    public void processCallBackQuery() {
        meetings = meetingService.getAllMeetings()
                .filter(m -> m.getMeetingDateTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Meeting::getMeetingDateTime).reversed())
                .collect(Collectors.toList());

        super.generateMainMessage(meetingMessageUtils::getPaginationAllViewArchived);
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}
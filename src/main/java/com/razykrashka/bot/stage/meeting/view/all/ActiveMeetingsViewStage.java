package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActiveMeetingsViewStage extends PaginationMeetingsViewStage {

    public static final List<String> KEYWORDS = Arrays.asList("/active", "/available");

    @Override
    public void processCallBackQuery() {
        meetings = meetingService.getAllActive()
                .sorted(Comparator.comparing(Meeting::getMeetingDateTime))
                .collect(Collectors.toList());
        super.generateMainMessage(meetingMessageUtils::getPaginationAllViewActive);
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || KEYWORDS.contains(updateHelper.getMessageText()))
                && !updateHelper.isMessageFromGroup();
    }
}
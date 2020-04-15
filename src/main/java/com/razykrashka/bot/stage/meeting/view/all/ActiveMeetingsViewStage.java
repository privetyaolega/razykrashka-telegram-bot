package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class ActiveMeetingsViewStage extends PaginationMeetingsViewStage {

    public static final String KEYWORD = "/active";

    @Override
    public boolean processCallBackQuery() {
        meetings = meetingService.getAllActive()
                .sorted(Comparator.comparing(Meeting::getMeetingDateTime))
                .collect(Collectors.toList());
        super.generateMainMessage(meetingMessageUtils::getPaginationAllViewActive);
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD);
    }
}
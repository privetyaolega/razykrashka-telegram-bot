package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class OfflineMeetingsViewStage extends PaginationMeetingsViewStage {

    public static final String KEYWORD = "/offline";

    @Override
    public void processCallBackQuery() {
        meetings = meetingService.getAllActive()
                .filter(m -> m.getFormat().equals(MeetingFormatEnum.OFFLINE))
                .sorted(Comparator.comparing(Meeting::getMeetingDateTime))
                .collect(Collectors.toList());
        super.generateMainMessage(meetingMessageUtils::getPaginationAllGeneral);
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD);
    }
}
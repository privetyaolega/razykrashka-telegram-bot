package com.razykrashka.bot.stage.meeting.view.all;

import org.springframework.stereotype.Component;

@Component
public class ArchivedMeetingsViewStage extends PaginationMeetingsViewStage {

    public static final String KEYWORD = "/archived";

    @Override
    public void processCallBackQuery() {
        meetings = meetingService.getAllArchivedMeetings();
        super.generateMainMessage(meetingMessageUtils::getPaginationAllViewArchived);
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}
package com.razykrashka.bot.stage.meeting.view.all;

import org.springframework.stereotype.Component;

@Component
public class ActiveMeetingsViewStage extends BaseMeetingsViewStage {

    @Override
    public boolean processCallBackQuery() {
        meetings = meetingRepository.findAllActiveAndDone();
        return super.processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
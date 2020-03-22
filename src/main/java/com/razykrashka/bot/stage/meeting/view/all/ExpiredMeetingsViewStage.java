package com.razykrashka.bot.stage.meeting.view.all;

import org.springframework.stereotype.Component;

@Component
public class ExpiredMeetingsViewStage extends BaseMeetingsViewStage {

    @Override
    public boolean processCallBackQuery() {
        meetings = meetingRepository.findAllExpired();
        return super.processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
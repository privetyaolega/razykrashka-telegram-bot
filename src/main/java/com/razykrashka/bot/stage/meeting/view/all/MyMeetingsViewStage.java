package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.view.utils.MeetingMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MyMeetingsViewStage extends BaseMeetingsViewStage {

    @Autowired
    private MeetingMessageUtils meetingMessageUtils;

    public MyMeetingsViewStage() {
        stageInfo = StageInfo.MY_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        meetings = meetingRepository.findAllScheduledMeetingsForUserById(updateHelper.getUser().getId());
        super.processCallBackQuery();
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains() || updateHelper.isMessageTextEquals(this.getStageInfo().getKeyword());
    }
}
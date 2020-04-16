package com.razykrashka.bot.stage.meeting.view.single.info;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SingleMeetingViewMapStage extends MainStage {

    @Override
    public void processCallBackQuery() {
        Integer meetingId = updateHelper.getIntegerPureCallBackData();
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Can not find meeting with id:" + meetingId));
        messageManager.sendMap(meeting);
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
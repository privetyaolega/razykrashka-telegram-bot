package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FinalMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptTopicMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        String topic = razykrashkaBot.getRealUpdate().getMessage().getText();
        Meeting meeting = getMeetingInCreation();
        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        meetingInfo.setTopic(topic);
        meetingInfo.setQuestions("");
        meetingInfoRepository.save(meetingInfo);
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null) {
            return false;
        }
        return super.getStageActivity();
    }
}
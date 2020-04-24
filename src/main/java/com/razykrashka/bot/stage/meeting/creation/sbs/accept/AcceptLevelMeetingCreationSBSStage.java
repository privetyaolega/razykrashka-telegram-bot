package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.db.entity.razykrashka.meeting.SpeakingLevel;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LocationMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.OfflineMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.ParticipantsMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptLevelMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void processCallBackQuery() {
        SpeakingLevel level = SpeakingLevel.valueOf(updateHelper.getCallBackData());
        MeetingInfo meetingInfo = MeetingInfo.builder()
                .speakingLevel(level)
                .build();
        meetingInfoRepository.save(meetingInfo);

        Meeting meeting = getMeetingInCreation();
        meeting.setMeetingInfo(meetingInfo);
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(ParticipantsMeetingCreationSBSStage.class).processCallBackQuery();
    }

    @Override
    public void handleRequest() {
        razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
        razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                && (!updateHelper.isCallBackDataContains(LocationMeetingCreationSBSStage.class.getSimpleName())
                && !updateHelper.isCallBackDataContains(OfflineMeetingCreationSBSStage.class.getSimpleName()));
    }
}
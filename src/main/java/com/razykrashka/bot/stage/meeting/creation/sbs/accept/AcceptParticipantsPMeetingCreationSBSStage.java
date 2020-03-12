package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.ParticipantsMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TopicMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptParticipantsPMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public boolean processCallBackQuery() {
        if (razykrashkaBot.getRealUpdate().hasMessage()) {
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            setActiveNextStage(ParticipantsMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(ParticipantsMeetingCreationSBSStage.class).handleRequest();
            return true;
        }
        Integer participantsLimit = updateHelper.getIntegerPureCallBackData();

        Meeting meeting = getMeetingInCreation();
        MeetingInfo meetingInfo = meeting.getMeetingInfo();
        meetingInfo.setParticipantLimit(participantsLimit);
        meetingInfoRepository.save(meetingInfo);
        meetingRepository.save(meeting);

        String messageInfoText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.updateMessage(messageInfoText);
        razykrashkaBot.getContext().getBean(TopicMeetingCreationSBSStage.class).handleRequest();
        return true;
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains() || super.isStageActive();
    }
}
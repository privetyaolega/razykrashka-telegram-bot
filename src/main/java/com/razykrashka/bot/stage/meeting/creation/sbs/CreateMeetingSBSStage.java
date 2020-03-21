package com.razykrashka.bot.stage.meeting.creation.sbs;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.*;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CreateMeetingSBSStage extends BaseMeetingCreationSBSStage {

    public CreateMeetingSBSStage() {
        stageInfo = StageInfo.CREATE_MEETING_ON_STEPS;
    }

    @Override
    public boolean processCallBackQuery() {
        messageManager.disableKeyboardLastBotMessage()
                .sendSimpleTextMessage("Start creation meeting step by step! Enjoy!");
        meeting = getMeetingInCreation();
        if (meeting.getCreationState().isInCreationProgress()) {
            setActiveNextStage(DateMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
        } else {
            String stage = meeting.getCreationState().getActiveStage();

            CreationState creationState = meeting.getCreationState();
            creationState.setInCreationProgress(true);
            creationStateRepository.save(creationState);
            meeting.setCreationState(creationState);
            meetingRepository.save(meeting);

            if (stage.contains(TimeMeetingCreationSBSStage.class.getSimpleName())) {
                razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
            } else if (stage.contains(ParticipantsMeetingCreationSBSStage.class.getSimpleName())) {
                razykrashkaBot.getContext().getBean(ParticipantsMeetingCreationSBSStage.class).handleRequest();
            } else if (stage.contains(LocationMeetingCreationSBSStage.class.getSimpleName())) {
                razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
            } else if (stage.contains(LevelMeetingCreationSBSStage.class.getSimpleName())) {
                razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).handleRequest();
            } else if (stage.contains(TopicMeetingCreationSBSStage.class.getSimpleName())) {
                razykrashkaBot.getContext().getBean(TopicMeetingCreationSBSStage.class).handleRequest();
            } else if (stage.contains(DateMeetingCreationSBSStage.class.getSimpleName())) {
                razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
            }
        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
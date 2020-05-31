package com.razykrashka.bot.stage.meeting.creation.sbs.start;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContinueCreationMeetingSBSStage extends BaseMeetingCreationSBSStage {

    List<Class<? extends BaseMeetingCreationSBSStage>> sbsStages = new ArrayList<>();

    public ContinueCreationMeetingSBSStage() {
        sbsStages.add(DateMeetingCreationSBSStage.class);
        sbsStages.add(TimeMeetingCreationSBSStage.class);
        sbsStages.add(FormatMeetingCreationSBSStage.class);
        sbsStages.add(OnlineMeetingCreationSBSStage.class);
        sbsStages.add(OfflineMeetingCreationSBSStage.class);
        sbsStages.add(LevelMeetingCreationSBSStage.class);
        sbsStages.add(ParticipantsMeetingCreationSBSStage.class);
        sbsStages.add(TopicMeetingCreationSBSStage.class);
        sbsStages.add(FinalMeetingCreationSBSStage.class);
    }

    @Override
    public void processCallBackQuery() {
        meeting = getMeetingInCreation();
        String stage = meeting.getCreationState().getActiveStage().replace("Accept", "");

        CreationState creationState = meeting.getCreationState();
        creationState.setInCreationProgress(true);
        creationStateRepository.save(creationState);
        meeting.setCreationState(creationState);
        meetingRepository.save(meeting);

        Class<? extends BaseMeetingCreationSBSStage> activeStage = sbsStages.stream()
                .filter(s -> s.getSimpleName().equals(stage))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("There is no SBS Stages by name: " + stage));
        super.setActiveNextStage(activeStage);
        razykrashkaBot.getContext().getBean(activeStage).handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataEquals();
    }
}
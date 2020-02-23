package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CreateMeetingOnStepsStage extends MainStage {
    public CreateMeetingOnStepsStage() {
        stageInfo = StageInfo.CREATE_MEETING_ON_STEPS;
    }
}
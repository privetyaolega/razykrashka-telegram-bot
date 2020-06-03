package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.IncorrectInputFormatSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LevelMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.OnlineMeetingCreationSBSStage;
import org.springframework.stereotype.Component;

@Component
public class AcceptOnlineMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        razykrashkaBot.getContext().getBean(IncorrectInputFormatSBSStage.class).handleRequest();
        razykrashkaBot.getContext().getBean(OnlineMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public void processCallBackQuery() {
        setActiveNextStage(LevelMeetingCreationSBSStage.class);
        razykrashkaBot.getContext().getBean(LevelMeetingCreationSBSStage.class).processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                && !updateHelper.isCallBackDataContains(EDIT)
                && !updateHelper.isCallBackDataContains(AcceptFormatMeetingCreationSBSStage.class.getSimpleName());
    }
}
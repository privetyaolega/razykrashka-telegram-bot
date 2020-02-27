package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FinalMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptTopicMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        String topic = razykrashkaBot.getMessageOptional().get().getText();
        super.getMeeting().getMeetingInfo().setTopic(topic);

        razykrashkaBot.getContext().getBean(FinalMeetingCreationSBSStage.class).handleRequest();
        super.setActiveNextStage(FinalMeetingCreationSBSStage.class);
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }
}
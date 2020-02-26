package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.stage.information.UndefinedStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LocationMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TimeMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AcceptTimeMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        // TODO (arutski): time validation;
        if (!razykrashkaBot.getMessageOptional().isPresent() // If data came from callBackData
                || !razykrashkaBot.getRealUpdate().getMessage().getText().matches(".*\\d.*") // Need to create regex for time
        ) {
            razykrashkaBot.getContext().getBean(UndefinedStage.class).handleRequest();
            setActiveNextStage(TimeMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
            return;
        }

        messageSender.deleteLastMessage();

        String time = razykrashkaBot.getMessageOptional().get().getText();
        super.getMeeting().setMeetingDateTime(super.getMeeting().getMeetingDateTime()
                .withHour(Integer.parseInt(time.substring(0, 2)))
                .withMinute(Integer.parseInt(time.substring(3))));

        super.setActiveNextStage(LocationMeetingCreationSBSStage.class);
    }


    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null) {
            return false;
        }
        return super.getStageActivity();
    }
}
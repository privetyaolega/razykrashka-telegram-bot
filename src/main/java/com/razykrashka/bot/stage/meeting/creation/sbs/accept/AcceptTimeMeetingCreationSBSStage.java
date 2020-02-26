package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

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
        String message = razykrashkaBot.getRealUpdate().getMessage().getText();
        if (!isStringTimeFormat(message)) {
            messageSender.sendSimpleTextMessage(String.format(super.getStringMap().get("incorrectTimeFormat"), message));
            razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
            return;
        }
        messageSender.deleteLastMessage();

        String time = razykrashkaBot.getMessageOptional().get().getText();
        super.getMeeting().setMeetingDateTime(super.getMeeting().getMeetingDateTime()
                .withHour(Integer.parseInt(time.substring(0, 2)))
                .withMinute(Integer.parseInt(time.substring(3))));
        razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
    }

    private boolean isStringTimeFormat(String time) {
        return time.matches("\\d{2}[:-]\\d{2}");
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null) {
            return false;
        }
        return super.getStageActivity();
    }
}
package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.exception.IncorrectInputDataFormat;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.LocationMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TimeMeetingCreationSBSStage;
import org.springframework.stereotype.Component;

@Component
public class AcceptTimeMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    private final static String TIME_REGEX = "^([0-1][0-9]|[2][0-3])[:-]([0-5][0-9])$";
    private String timeMessage;

    @Override
    public void handleRequest() {
        timeMessage = razykrashkaBot.getRealUpdate().getMessage().getText();
        inputDataValidation();
        super.getMeeting().setMeetingDateTime(super.getMeeting().getMeetingDateTime()
                .withHour(Integer.parseInt(timeMessage.substring(0, 2)))
                .withMinute(Integer.parseInt(timeMessage.substring(3))));
        razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
    }

    private void inputDataValidation() {
        if (!timeMessage.matches(TIME_REGEX)) {
            String message = String.format(super.getStringMap().get("incorrectTimeFormat"), timeMessage);
            messageSender.sendSimpleTextMessage(message);
            razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
            throw new IncorrectInputDataFormat(timeMessage + ": incorrect time format!");
        }
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() != null) {
            return false;
        }
        return super.getStageActivity();
    }
}
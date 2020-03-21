package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.exception.IncorrectInputDataFormatException;
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
        timeMessage = updateHelper.getMessageText();
        inputDataValidation();

        Meeting meeting = super.getMeetingInCreation();
        meeting.setMeetingDateTime(meeting.getMeetingDateTime()
                .withHour(Integer.parseInt(timeMessage.substring(0, 2)))
                .withMinute(Integer.parseInt(timeMessage.substring(3))));
        meetingRepository.save(meeting);

        messageManager.deleteLastMessage()
                .deleteLastBotMessage();
        razykrashkaBot.getContext().getBean(LocationMeetingCreationSBSStage.class).handleRequest();
    }

    private void inputDataValidation() {
        if (!timeMessage.matches(TIME_REGEX)) {
            String message = String.format(super.getStringMap().get("incorrectTimeFormat"), timeMessage);
            messageManager.disableKeyboardLastBotMessage()
                    .replyLastMessage(message);
            razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
            throw new IncorrectInputDataFormatException(timeMessage + ": incorrect time format!");
        }
    }

    @Override
    public boolean isStageActive() {
        return !updateHelper.hasCallBackQuery() && super.isStageActive();
    }
}
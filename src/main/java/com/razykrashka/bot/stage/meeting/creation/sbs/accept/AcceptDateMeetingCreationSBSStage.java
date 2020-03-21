package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.exception.IncorrectInputDataFormatException;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.DateMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TimeMeetingCreationSBSStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptDateMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    LocalDateTime localDateTime;

    @Override
    public boolean processCallBackQuery() {
        messageInputHandler();

        String ddMMyyyy = updateHelper.getCallBackData();
        localDateTime = LocalDateTime.of(Integer.parseInt(ddMMyyyy.substring(4)),
                Integer.parseInt(ddMMyyyy.substring(2, 4)),
                Integer.parseInt(ddMMyyyy.substring(0, 2)), 0, 0);
        pastDateHandler();

        Meeting meeting = getMeetingInCreation();
        meeting.setMeetingDateTime(localDateTime.withHour(0).withMinute(0));
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
        return true;
    }

    private void messageInputHandler() {
        if (razykrashkaBot.getRealUpdate().hasMessage()) {
            messageManager.disableKeyboardLastBotMessage();
            setActiveNextStage(DateMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
            throw new IncorrectInputDataFormatException("Stage does not support text message date input!");
        }
    }

    private void pastDateHandler() {
        if (localDateTime.isBefore(LocalDateTime.now())) {
            messageManager.sendAlertMessage("ERROR! Impossible to create meeting in the past.\uD83D\uDE30");
            setActiveNextStage(DateMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
            throw new IncorrectInputDataFormatException("Selected date is in the past!");
        }
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        if (updateHelper.isCallBackDataContains(DateMeetingCreationSBSStage.class.getSimpleName())) {
            return false;
        }
        return super.isStageActive();
    }
}
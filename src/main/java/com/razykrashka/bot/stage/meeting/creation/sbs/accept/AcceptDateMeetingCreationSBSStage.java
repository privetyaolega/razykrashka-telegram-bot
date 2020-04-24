package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.exception.IncorrectInputDataFormatException;
import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.DateMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TimeMeetingCreationSBSStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptDateMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Autowired
    MeetingProperties meetingProperties;

    @Override
    public void processCallBackQuery() {
        messageInputHandler();

        LocalDateTime localDateTime = getLocalDateTime();
        Meeting meeting = getMeetingInCreation();
        meeting.setMeetingDateTime(localDateTime);
        meetingRepository.save(meeting);

        razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).processCallBackQuery();
    }

    private void messageInputHandler() {
        if (razykrashkaBot.getRealUpdate().hasMessage()) {
            messageManager.disableKeyboardLastBotMessage();
            setActiveNextStage(DateMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
            throw new IncorrectInputDataFormatException("Stage does not support text message date input!");
        }
    }

    private LocalDateTime getLocalDateTime() {
        String ddMMyyyy = updateHelper.getCallBackData();
        LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(ddMMyyyy.substring(4)),
                Integer.parseInt(ddMMyyyy.substring(2, 4)),
                Integer.parseInt(ddMMyyyy.substring(0, 2)), 0, 0);

        boolean isMeetingDateToday = localDateTime.toLocalDate().isEqual(LocalDate.now());
        if (localDateTime.isBefore(LocalDateTime.now()) && !isMeetingDateToday) {
            sendAlertMessage(getString("pastDate"), false);
            throw new IncorrectInputDataFormatException("Accept time Exception: Selected date is in the past!");
        } else if (isMeetingDateToday
                && LocalDateTime.now().getHour() > meetingProperties.getCreation().getUpperHourLimitToday()) {
            sendAlertMessage(getString("lateTime"), true);
            throw new IncorrectInputDataFormatException("Accept time Exception: Too late for meeting today");
        } else if (LocalDateTime.now().plusDays(meetingProperties.getCreation().getDaysAdvanceMaximum())
                .isBefore(localDateTime)) {
            sendAlertMessage(getFormatString("lateDaysWarning", meetingProperties.getCreation().getDaysAdvanceMaximum()), true);
            throw new IncorrectInputDataFormatException("Meeting too far in the future: ");
        }
        return localDateTime.withHour(0).withMinute(0);
    }

    private void sendAlertMessage(String message, boolean showAlert) {
        messageManager.sendAlertMessage(message, showAlert);
        setActiveNextStage(DateMeetingCreationSBSStage.class);
        razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return !updateHelper.isCallBackDataContains(DateMeetingCreationSBSStage.class.getSimpleName())
                && super.isStageActive();
    }
}
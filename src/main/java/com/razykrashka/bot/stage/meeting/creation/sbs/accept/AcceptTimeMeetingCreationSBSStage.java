package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.exception.IncorrectInputDataFormatException;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.FormatMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.TimeMeetingCreationSBSStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptTimeMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    final static String TIME_REGEX = "^([0-1][0-9]|[2][0-3])[:-]([0-5][0-9])$";

    @Value("${razykrashka.bot.meeting.creation.hour-advance}")
    Integer hourAdvance;

    @Override
    public void handleRequest() {
        meeting = super.getMeetingInCreation();
        LocalDateTime localDateTime = getMeetingDateTime();
        meeting.setMeetingDateTime(localDateTime);
        meetingRepository.save(meeting);

        messageManager.deleteLastMessage()
                .deleteLastBotMessage();
        razykrashkaBot.getContext().getBean(FormatMeetingCreationSBSStage.class).handleRequest();
    }

    public void handleRequest(String input) {
        meeting = super.getMeetingInCreation();
        LocalDateTime localDateTime = meeting.getMeetingDateTime()
                .withHour(Integer.parseInt(input.substring(0, 2)))
                .withMinute(Integer.parseInt(input.substring(2)));
        meeting = super.getMeetingInCreation();
        meeting.setMeetingDateTime(localDateTime);
        meetingRepository.save(meeting);
        razykrashkaBot.getContext().getBean(FormatMeetingCreationSBSStage.class).handleRequest();
    }

    private LocalDateTime getMeetingDateTime() {
        String timeMessage = updateHelper.getMessageText();
        boolean isMeetingDateToday = meeting.getMeetingDateTime().toLocalDate().isEqual(LocalDate.now());
        if (!timeMessage.matches(TIME_REGEX)) {
            String message = getFormatString("incorrectTimeFormat", timeMessage);
            sendReplyErrorMessage(message);
            throw new IncorrectInputDataFormatException(timeMessage + ": incorrect time format!");
        }

        LocalDateTime meetingDateTime = meeting.getMeetingDateTime()
                .withHour(Integer.parseInt(timeMessage.substring(0, 2)))
                .withMinute(Integer.parseInt(timeMessage.substring(3)));

        if (isMeetingDateToday && meetingDateTime.isBefore(LocalDateTime.now())) {
            sendReplyErrorMessage(getString("pastTime"));
            throw new IncorrectInputDataFormatException("Attempt to create meeting in the past! Entered time: " + timeMessage);
        } else if (isMeetingDateToday && meetingDateTime.isBefore(LocalDateTime.now().plusHours(hourAdvance))) {
            String minimumTimeForCreation = LocalDateTime.now()
                    .plusHours(hourAdvance)
                    .plusMinutes(5)
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
            String message = getFormatString("hourAdvance", hourAdvance, minimumTimeForCreation);
            sendReplyErrorMessage(message);
            throw new IncorrectInputDataFormatException("Too late for meeting creation");
        }
        return meetingDateTime;
    }

    private void sendReplyErrorMessage(String message) {
        messageManager.disableKeyboardLastBotMessage()
                .replyLastMessage(message);
        razykrashkaBot.getContext().getBean(TimeMeetingCreationSBSStage.class).handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return !updateHelper.hasCallBackQuery()
                && super.isStageActive();
    }
}
package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptTimeMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class TimeMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        meeting.setMeetingDateTime(meeting.getMeetingDateTime()
                .withHour(0)
                .withMinute(0));
        meetingRepository.save(meeting);

        messageManager.deleteLastBotMessageIfHasKeyboard();
        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        messageManager.sendSimpleTextMessage(messageText +
                "Please, choose time (e.g 19-30)", getKeyboard());
        super.setActiveNextStage(AcceptTimeMeetingCreationSBSStage.class);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(getString("backButton"), DateMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataContains();
    }
}
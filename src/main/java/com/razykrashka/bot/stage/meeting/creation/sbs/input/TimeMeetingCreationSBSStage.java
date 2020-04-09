package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptTimeMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
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

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
        messageManager.deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(messageText, getKeyboard());
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
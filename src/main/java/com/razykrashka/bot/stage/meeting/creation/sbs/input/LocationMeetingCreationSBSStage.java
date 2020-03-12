package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLocationMeetingCreationStepByStep;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class LocationMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        messageManager.deleteLastBotMessageIfHasKeyboard();

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(getMeetingInCreation());
        messageManager.sendSimpleTextMessage(messageText +
                "Please, attach or write location (e.g ул. Немига 6)", getKeyboard());
        super.setActiveNextStage(AcceptLocationMeetingCreationStepByStep.class);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow("BACK TO TIME EDIT", TimeMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive() || updateHelper.isCallBackDataContains();
    }
}
package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptFinalMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
public class FinalMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        messageManager.deleteLastBotMessageIfHasKeyboard();
        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow(getString("confirmButton"), AcceptFinalMeetingCreationSBSStage.class.getSimpleName())
                .setRow(getString("backButton"), TopicMeetingCreationSBSStage.class.getSimpleName())
                .build();

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(getMeetingInCreation());
        messageManager.sendSimpleTextMessage(messageText + getString("input"), keyboardMarkup);
        setActiveNextStage(AcceptFinalMeetingCreationSBSStage.class);
    }
}
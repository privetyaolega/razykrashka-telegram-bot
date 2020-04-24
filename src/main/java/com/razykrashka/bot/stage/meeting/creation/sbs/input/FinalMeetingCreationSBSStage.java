package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptFinalMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
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
                .setRow(getString("confirmButton") + Emoji.OK_HAND, AcceptFinalMeetingCreationSBSStage.class.getSimpleName())
                .setRow(getString("backButton"), TopicMeetingCreationSBSStage.class.getSimpleName() + EDIT)
                .build();

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(getMeetingInCreation())
                + TextFormatter.getItalicString(getString("input"));
        messageManager
                .disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(messageText, keyboardMarkup);
        setActiveNextStage(AcceptFinalMeetingCreationSBSStage.class);
    }
}
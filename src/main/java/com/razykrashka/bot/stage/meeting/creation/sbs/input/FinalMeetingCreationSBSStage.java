package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptFinalMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class FinalMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptFinalMeetingCreationSBSStage.class;
    Class<? extends BaseMeetingCreationSBSStage> previousStageClass = TopicMeetingCreationSBSStage.class;

    @Override
    public void handleRequest() {
        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(getMeetingInCreation())
                + TextFormatter.getItalicString(getString("input"));
        messageManager.updateMessage(messageText, getKeyboard());
        setActiveNextStage(nextStageClass);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(getString("confirmButton") + Emoji.OK_HAND, nextStageClass.getSimpleName())
                .setRow(getString("backButton"), previousStageClass.getSimpleName() + EDIT)
                .build();
    }
}
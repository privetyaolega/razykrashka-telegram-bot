package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptOnlineMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OnlineMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptOnlineMeetingCreationSBSStage.class;
    Class<? extends BaseMeetingCreationSBSStage> previousStageClass = FormatMeetingCreationSBSStage.class;

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(getMessage(), getKeyboard());
        setActiveNextStage(nextStageClass);
    }

    @Override
    public void processCallBackQuery() {
        messageManager.updateOrSendDependsOnLastMessageOwner(getMessage(), getKeyboard());
        setActiveNextStage(nextStageClass);
    }

    private String getMessage() {
        meeting = getMeetingInCreation();
        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        return messageText + TextFormatter.getItalicString(getString("input"));
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow("Got it! Move on! " + Emoji.SMILING_DEVIL, nextStageClass.getSimpleName())
                .setRow(getString("backButton"), previousStageClass.getSimpleName() + EDIT)
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataEquals(this.getClass().getSimpleName() + EDIT);
    }
}
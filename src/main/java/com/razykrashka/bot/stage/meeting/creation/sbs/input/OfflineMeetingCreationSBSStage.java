package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptOfflineMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfflineMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    Class<? extends BaseMeetingCreationSBSStage> nextStageClass = AcceptOfflineMeetingCreationSBSStage.class;
    Class<? extends BaseMeetingCreationSBSStage> previousStageClass = FormatMeetingCreationSBSStage.class;

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        String skype = updateHelper.getUser().getSkypeContact();

        KeyboardBuilder keyboard = keyboardBuilder.getKeyboard();
        String message;
        if (skype == null) {
            keyboard.setRow(getString("backButton"), previousStageClass.getSimpleName() + EDIT)
                    .build();
            message = messageText + getString("input");
        } else {
            keyboard.setRow(ImmutableMap.of(
                    getString(EDIT), nextStageClass.getSimpleName(),
                    getString("confirmButton"), nextStageClass.getSimpleName() + "Confirm"))
                    .setRow(getString("backButton"), previousStageClass.getSimpleName() + EDIT)
                    .build();
            message = messageText + "Your Skype account is " + TextFormatter.getCodeString(skype);
        }
        messageManager.updateOrSendDependsOnLastMessageOwner(message, keyboard.build());
        setActiveNextStage(nextStageClass);
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataEquals(this.getClass().getSimpleName() + EDIT);
    }
}
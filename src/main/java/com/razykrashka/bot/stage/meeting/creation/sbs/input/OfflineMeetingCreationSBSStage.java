package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptOfflineMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
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

        if (skype == null) {
            messageManager.updateOrSendDependsOnLastMessageOwner(messageText + getString("input"), null);
        } else {
            messageManager.updateOrSendDependsOnLastMessageOwner(messageText +
                    "Your Skype account is " + TextFormatter.getCodeString(skype), this.getKeyboard());
        }
        setActiveNextStage(nextStageClass);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow(ImmutableMap.of(
                        getString("edit"), nextStageClass.getSimpleName(),
                        getString("confirmButton"), nextStageClass.getSimpleName() + "Confirm"))
                .setRow(getString("backButton"), previousStageClass.getSimpleName() + "edit")
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataEquals(this.getClass().getSimpleName() + "edit");
    }
}
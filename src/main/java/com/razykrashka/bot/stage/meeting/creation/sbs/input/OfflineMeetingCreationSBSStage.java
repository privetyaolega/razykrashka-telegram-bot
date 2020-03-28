package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptOfflineMeetingCreationSBSStage;
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

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting);
        String skype = updateHelper.getUser().getSkypeContact();

        if (skype == null) {
            messageManager.updateOrSendDependsOnLastMessageOwner(messageText + "Please, enter you Skype account name.", null);
        } else {
            messageManager.updateOrSendDependsOnLastMessageOwner(messageText + "Your Skype account is " + skype, this.getKeyboard());
        }
        setActiveNextStage(nextStageClass);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow(ImmutableMap.of(
                        "Confirm", nextStageClass.getSimpleName() + "Confirm",
                        "Edit Skype", nextStageClass.getSimpleName()))
                .setRow("Back to Meeting format edit", FormatMeetingCreationSBSStage.class.getSimpleName() + "edit")
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataEquals(this.getClass().getSimpleName() + "edit");
    }
}
package com.razykrashka.bot.stage.meeting.creation.sbs.start;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyMeetingStateSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public boolean processCallBackQuery() {
        meeting = getMeetingInCreation();
        if (meeting.getCreationState().isInCreationProgress()) {
            setActiveNextStage(StartNewMeetingCreationSBSStage.class);
            razykrashkaBot.getContext().getBean(StartNewMeetingCreationSBSStage.class).processCallBackQuery();
        } else {
            InlineKeyboardMarkup keyboard = keyboardBuilder.getKeyboard()
                    .setRow(getString("continue"), ContinueCreationMeetingSBSStage.class.getSimpleName())
                    .setRow(getString("create"), StartNewMeetingCreationSBSStage.class.getSimpleName())
                    .build();
            messageManager.disableKeyboardLastBotMessage()
                    .sendSimpleTextMessage(getString("main"), keyboard);
        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptFinalFMeetingCreationStepByStep;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
public class FinalMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow("Confirm", AcceptFinalFMeetingCreationStepByStep.class.getSimpleName())
                .setRow("BACK TO TOPIC EDIT", TopicMeetingCreationSBSStage.class.getSimpleName())
                .build();
        messageSender.updateMessage(getMeetingPrettyString() +
                "\nPlease, confirm meeting creation.", keyboardMarkup);
        setActiveNextStage(AcceptFinalFMeetingCreationStepByStep.class);
    }
}
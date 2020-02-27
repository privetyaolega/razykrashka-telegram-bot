package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptTopicMeetingCreationStepByStep;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
public class TopicMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        messageSender.updateMessage(super.getMeetingPrettyString() +
                "\n\nPlease, input topic", (InlineKeyboardMarkup) getKeyboard());
        super.setActiveNextStage(AcceptTopicMeetingCreationStepByStep.class);
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow("BACK TO PARTICIPANT LIMIT EDIT", ParticipantsMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        return super.getStageActivity();
    }
}
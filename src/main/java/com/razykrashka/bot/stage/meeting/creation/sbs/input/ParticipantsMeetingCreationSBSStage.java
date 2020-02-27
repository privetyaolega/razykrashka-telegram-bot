package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptLocationMeetingCreationStepByStep;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptParticipantsMeetingCreationSBSStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Log4j2
@Component
public class ParticipantsMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        InlineKeyboardMarkup keyboardMarkup = keyboardBuilder.getKeyboard()
                .setRow(ImmutableMap.of(
                        "1", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "1",
                        "2", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() + "2"))
                .setRow(ImmutableMap.of(
                        "3", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() +"3",
                        "4", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() +"4"))
                .setRow(ImmutableMap.of(
                        "5", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() +"5",
                        "6", AcceptParticipantsMeetingCreationSBSStage.class.getSimpleName() +"6"))
                .setRow("BACK TO LEVEL EDIT", LevelMeetingCreationSBSStage.class.getSimpleName())
                .build();
        messageSender.updateMessage(getMeetingPrettyString() + "\n\nPlease, input max people", keyboardMarkup);
        super.setActiveNextStage(AcceptLocationMeetingCreationStepByStep.class);
    }

    @Override
    public boolean isStageActive() {
        return super.getStageActivity();
    }
}
package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptParticipantsPMeetingCreationSBSStage;
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
                        "1", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "1",
                        "2", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "2"))
                .setRow(ImmutableMap.of(
                        "3", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "3",
                        "4", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "4"))
                .setRow(ImmutableMap.of(
                        "5", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "5",
                        "6", AcceptParticipantsPMeetingCreationSBSStage.class.getSimpleName() + "6"))
                .setRow("BACK TO LEVEL EDIT", LevelMeetingCreationSBSStage.class.getSimpleName())
                .build();
        messageSender.sendSimpleTextMessage(getMeetingPrettyString() + "\n\nPlease, input max people", keyboardMarkup);
        super.setActiveNextStage(AcceptParticipantsPMeetingCreationSBSStage.class);
    }

    @Override
    public boolean isStageActive() {
        return getStageActivity();
    }
}
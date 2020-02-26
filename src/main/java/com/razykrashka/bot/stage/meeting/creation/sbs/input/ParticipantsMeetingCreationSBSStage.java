package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
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
                        "1", "1",
                        "2", "2"))
                .setRow(ImmutableMap.of(
                        "3", "3",
                        "4", "4"))
                .setRow(ImmutableMap.of(
                        "5", "5",
                        "6", "6"))
                .build();
        messageSender.updateMessage(getMeetingPrettyString() + "\n\nPlease, input max people", keyboardMarkup);
        this.setActive(false);
    }
}
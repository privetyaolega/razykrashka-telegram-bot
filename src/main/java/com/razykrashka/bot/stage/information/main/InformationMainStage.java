package com.razykrashka.bot.stage.information.main;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.stage.MainStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;


@Component
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class InformationMainStage extends MainStage {

    static final String HIGHLIGHT_LEFT_BORDER = "⤙ ";
    static final String HIGHLIGHT_RIGHT_BORDER = " ⤚";
    String buttonLabel;

    protected ReplyKeyboard getKeyboardWithHighlightedButton() {
        return keyboardBuilder
                .getKeyboard()
                .setRow("Main", InformationStage.class.getSimpleName())
                .setRow(ImmutableMap.of(
                        "Stats", StatisticStage.class.getSimpleName(),
                        "Support Us", SupportUsStage.class.getSimpleName(),
                        "Contacts", ContactsStage.class.getSimpleName()
                ))
                .setRow("Help", HelpStage.class.getSimpleName())
                .highlightButtonWithText(buttonLabel, HIGHLIGHT_LEFT_BORDER, HIGHLIGHT_RIGHT_BORDER)
                .build();
    }
}
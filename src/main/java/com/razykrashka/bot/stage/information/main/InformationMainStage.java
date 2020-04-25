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

    protected ReplyKeyboard getKeyboardWithHighlightedButton(String buttonLabel) {
        return keyboardBuilder
                .getKeyboard()
                .setRow(ImmutableMap.of(
                        "Main", InformationStage.class.getSimpleName(),
                        "Statistics", StatisticStage.class.getSimpleName()
                ))
                .setRow("Help", HelpStage.class.getSimpleName())
                .highlightButtonWithText(buttonLabel, HIGHLIGHT_LEFT_BORDER, HIGHLIGHT_RIGHT_BORDER)
                .build();
    }
}
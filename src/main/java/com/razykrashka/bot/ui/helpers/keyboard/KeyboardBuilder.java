package com.razykrashka.bot.ui.helpers.keyboard;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class KeyboardBuilder {

    InlineKeyboardMarkup keyboard;
    List<InlineKeyboardButton> row;
    InlineKeyboardButton button;
    List<List<InlineKeyboardButton>> inlineKeyboardButtons;

    public KeyboardBuilder getKeyboard() {
        keyboard = new InlineKeyboardMarkup();
        inlineKeyboardButtons = new ArrayList<>();
        return this;
    }

    public KeyboardBuilder setRow(Map<String, String> textCallBackMap) {
        row = new ArrayList<>();
        textCallBackMap.forEach((key, value) -> {
            button = new InlineKeyboardButton()
                    .setText(key)
                    .setCallbackData(value);
            row.add(button);
        });
        inlineKeyboardButtons.add(row);
        return this;
    }

    public KeyboardBuilder setRow(String buttonText, String callBackData) {
        row = new ArrayList<>();
        button = new InlineKeyboardButton()
                .setText(buttonText)
                .setCallbackData(callBackData);
        row.add(button);
        inlineKeyboardButtons.add(row);
        return this;
    }

    public KeyboardBuilder setRow(List<Pair<String, String>> textCallBackList) {
        row = new ArrayList<>();
        for (Pair pair : textCallBackList) {
            button = new InlineKeyboardButton()
                    .setText((String) pair.getFirst())
                    .setCallbackData(String.valueOf(pair.getSecond()));
            row.add(button);
        }

        inlineKeyboardButtons.add(row);
        return this;
    }

    public KeyboardBuilder setRow(InlineKeyboardButton button) {
        inlineKeyboardButtons.add(Collections.singletonList(button));
        return this;
    }

    public InlineKeyboardMarkup build() {
        this.keyboard.setKeyboard(inlineKeyboardButtons);
        return keyboard;
    }

    public InlineKeyboardMarkup getPaginationKeyboard(Class classCaller, int pageNumToShow, int totalPagesSize) {
        List<Pair<String, String>> listPair = PaginationKeyboardHelper.builder()
                .classCaller(classCaller)
                .pageNumToShow(pageNumToShow)
                .totalPagesSize(totalPagesSize)
                .build()
                .getPaginationKeyboard();
        return getKeyboard().setRow(listPair).build();
    }
}
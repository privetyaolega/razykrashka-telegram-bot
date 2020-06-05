package com.razykrashka.bot.stage.meeting.view.all;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.MainStage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SelectMeetingsTypeStage extends MainStage {

    public static final String KEYWORD = "View Meetings";

    @Override
    public void handleRequest() {
        messageManager.disableKeyboardLastBotMessage()
                .sendSimpleTextMessage(getString("main"), this.getKeyboard());
    }

    @Override
    public void processCallBackQuery() {
        handleRequest();
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow(ImmutableMap.of(
                        "Offline " + Emoji.COFFEE, OfflineMeetingsViewStage.class.getSimpleName(),
                        "Online " + Emoji.INTERNET, OnlineMeetingsViewStage.class.getSimpleName())
                )
                .setRow("Offline & Online " + Emoji.BOOM, ActiveMeetingsViewStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isMessageTextEquals(KEYWORD)
                || updateHelper.isCallBackDataContains())
                && !updateHelper.isMessageFromGroup();
    }
}
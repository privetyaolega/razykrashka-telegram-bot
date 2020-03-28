package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SelectMeetingsTypeStage extends MainStage {

    public SelectMeetingsTypeStage() {
        stageInfo = StageInfo.SELECT_MEETINGS_TYPE;
    }

    @Override
    public void handleRequest() {
        messageManager.sendSimpleTextMessage(getString("main"), this.getKeyboard());
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder
                .getKeyboard()
                .setRow("Offline " + Emoji.COFFEE, OfflineMeetingsViewStage.class.getSimpleName())
                .setRow("Online " + Emoji.INTERNET, OnlineMeetingsViewStage.class.getSimpleName())
                .build();
    }
}
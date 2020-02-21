package com.razykrashka.bot.stage;

import com.razykrashka.bot.model.telegram.TelegramUpdate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

public interface Stage {

    StageInfo getStageInfo();

    ReplyKeyboard getKeyboard();

    List<String> getValidKeywords();

    void handleRequest();

    boolean isStageActive();

    Stage setMessage(TelegramUpdate message);

    boolean processCallBackQuery();
}

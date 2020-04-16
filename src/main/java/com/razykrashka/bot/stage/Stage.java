package com.razykrashka.bot.stage;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface Stage {

    boolean isStageActive();

    void handleRequest();

    void processCallBackQuery();

    ReplyKeyboard getKeyboard();
}
package com.razykrashka.bot.stage;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface Stage {

    StageInfo getStageInfo();

    ReplyKeyboard getKeyboard();

    void handleRequest();

    boolean isStageActive();

    boolean processCallBackQuery();

    void setActive(boolean isActive);
}

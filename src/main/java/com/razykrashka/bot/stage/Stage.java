package com.razykrashka.bot.stage;

import com.razykrashka.bot.entity.Meeting;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

public interface Stage {

    StageInfo getStageInfo();

    ReplyKeyboard getKeyboard();

    ReplyKeyboard getKeyboard(Meeting model);

    List<String> getValidKeywords();

    void handleRequest();

    boolean isStageActive();

    Stage setMessage(Update message);

    boolean processCallBackQuery();

    void setActive(boolean isActive);
}

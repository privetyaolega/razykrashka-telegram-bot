package com.razykrashka.bot.hendler;

import com.razykrashka.bot.model.telegram.TelegramUpdate;

public interface TelegramMessageHandler {
    void handle(TelegramUpdate telegramUpdate);
}

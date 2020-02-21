package com.razykrashka.bot.hendler;

import com.razykrashka.bot.model.telegram.TelegramUpdate;
import com.razykrashka.bot.model.telegram.TelegramUser;
import com.razykrashka.bot.repository.json.GsonHelper;
import com.razykrashka.bot.service.Avp256Bot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CreateUserMessageHandler implements TelegramMessageHandler {
    Avp256Bot avp256Bot;
    GsonHelper gsonHelper;

    @Override
    public void handle(TelegramUpdate telegramUpdate) {
        if (telegramUpdate.getMessage().getText().startsWith("create")) {
            Long chatId = telegramUpdate.getMessage().getChat().getId();
            TelegramUser user = telegramUpdate.getMessage().getFrom();

//            gsonHelper.writeToFile(new TestModel("test", telegramUpdate.getMessage().getText()));


            avp256Bot.sendTextMessage(chatId, gsonHelper.readFromFile().toString());
        }
    }
}

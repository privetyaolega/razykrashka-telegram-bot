package com.razykrashka.bot.hendler;

import com.razykrashka.bot.model.telegram.TelegramUpdate;
import com.razykrashka.bot.model.telegram.TelegramUser;
import com.razykrashka.bot.repository.PersonRepository;
import com.razykrashka.bot.repository.telegram.TelegramUserRepository;
import com.razykrashka.bot.service.Avp256Bot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthTelegramMessageHandler implements TelegramMessageHandler {
    Avp256Bot avp256Bot;
    PersonRepository personRepository;
    TelegramUserRepository telegramUserRepository;

    @Override
    public void handle(TelegramUpdate telegramUpdate) {
        if (!telegramUpdate.getMessage().getText().startsWith(Avp256Bot.START_COMMAND)
                || Objects.nonNull(telegramUpdate.getMessage().getFrom().getPerson())) {
            return;
        }
        String authCode = telegramUpdate.getMessage().getText().replace(Avp256Bot.START_COMMAND, "").trim();
        personRepository.findByAuthCode(authCode)
                .ifPresent(person -> {
                    TelegramUser user = telegramUpdate.getMessage().getFrom();
                    user.setPerson(person);
                    telegramUserRepository.save(user);

                    Long chatId = telegramUpdate.getMessage().getChat().getId();
                    String text = "You have been authorized as " + person.getName();
                    avp256Bot.sendTextMessage(chatId, text);
                });
    }
}

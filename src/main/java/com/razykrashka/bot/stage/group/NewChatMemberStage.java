package com.razykrashka.bot.stage.group;


import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.stage.MainStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewChatMemberStage extends MainStage {

    @Override
    public void handleRequest() {
        Integer id = updateHelper.getUpdate().getMessage().getFrom().getId();
        Optional<TelegramUser> userEntity = telegramUserRepository.findById(id);
        TelegramUser telegramUser;
        if (userEntity.isPresent()) {
            telegramUser = userEntity.get();
        } else {
            User userTelegram = updateHelper.getUpdate().getMessage().getFrom();
            telegramUser = TelegramUser.builder()
                    .lastName(userTelegram.getLastName())
                    .firstName(userTelegram.getFirstName())
                    .userName(userTelegram.getUserName())
                    .id(userTelegram.getId())
                    .build();
            telegramUserRepository.save(telegramUser);
        }

        messageManager.sendMessage(new SendMessage()
                .setChatId(String.valueOf(telegramUser.getId()))
                .setText(super.getString("welcome")));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isNewChatMember();
    }
}
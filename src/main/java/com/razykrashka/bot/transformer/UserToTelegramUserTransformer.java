package com.razykrashka.bot.transformer;

import com.razykrashka.bot.model.telegram.TelegramUserNOT;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

@Component
public class UserToTelegramUserTransformer implements Transformer<User, TelegramUserNOT> {
    @Override
    public TelegramUserNOT transform(User user) {
        return TelegramUserNOT.builder()
                .id(user.getId())
                .creationDate(LocalDateTime.now())
                .userName(user.getUserName())
                .bot(user.getBot())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .languageCode(user.getLanguageCode())
                .build();
    }
}

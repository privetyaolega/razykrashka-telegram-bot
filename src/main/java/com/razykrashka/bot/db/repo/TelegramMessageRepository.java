package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramMessageRepository extends CrudRepository<TelegramMessage, Long> {
    Optional<TelegramMessage> findById(Long id);

    List<TelegramMessage> findAllByBotMessageIsTrueAndChatIdEquals(Long chatId);

    List<TelegramMessage> findAllByChatIdEquals(Long chatId);

    TelegramMessage findTop1ByChatIdOrderByIdDesc(Long id);

    TelegramMessage findTop1ByChatIdAndBotMessageIsTrueOrderByIdDesc(Long id);
}

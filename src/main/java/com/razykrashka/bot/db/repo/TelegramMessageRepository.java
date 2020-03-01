package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.telegram.TelegramMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TelegramMessageRepository extends CrudRepository<TelegramMessage, Long> {

    Optional<TelegramMessage> findById(Long id);

    List<TelegramMessage> findAllByBotMessageIsTrue();
    List<TelegramMessage> findAllByChatIdEquals(Long chatId);

}

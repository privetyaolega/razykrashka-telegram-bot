package com.razykrashka.bot.repository;

import com.razykrashka.bot.entity.TelegramUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TelegramUserRepository extends CrudRepository<TelegramUser, Integer> {
    Optional<TelegramUser> findByTelegramId(Integer id);
}

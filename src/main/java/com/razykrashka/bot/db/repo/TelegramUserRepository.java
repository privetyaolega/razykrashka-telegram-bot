package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import org.springframework.data.repository.CrudRepository;

public interface TelegramUserRepository extends CrudRepository<TelegramUser, Integer> {

}
package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TelegramUserRepository extends CrudRepository<TelegramUser, Integer> {

    // If we have two users with the same telegram id we'll get javax.persistence.NonUniqueResultException: query did not return a unique result
    @Query(value = "SELECT * FROM user WHERE user.telegram_id = ?1 LIMIT 1", nativeQuery = true)
    Optional<TelegramUser> findByTelegramId(Integer id);
}
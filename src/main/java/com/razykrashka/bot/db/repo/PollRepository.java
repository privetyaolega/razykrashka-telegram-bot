package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPoll;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PollRepository extends CrudRepository<TelegramPoll, Integer> {

    Optional<TelegramPoll> findByTelegramIdEquals(String telegramId);

    @Query(value = "SELECT * " +
            "FROM poll " +
            "ORDER BY creation_date_time " +
            "DESC LIMIT 1", nativeQuery = true)
    Optional<TelegramPoll> getLastCreatedPoll();
}
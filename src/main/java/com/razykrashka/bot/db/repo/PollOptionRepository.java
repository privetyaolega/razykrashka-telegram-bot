package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPollOption;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PollOptionRepository extends CrudRepository<TelegramPollOption, Integer> {

    @Query(value = "SELECT * FROM poll_option o " +
            "INNER JOIN poll p " +
            "ON o.poll_id = p.id " +
            "WHERE telegram_id = ?1 " +
            "AND text_option = ?2", nativeQuery = true)
    Optional<TelegramPollOption> findByTelegramIdAndTextOption(String telegramId, String textOption);
}
package com.razykrashka.bot.db.repo;


import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {

    List<Meeting> findAllByTelegramUser(TelegramUser telegramUser);

    @Query(value = "SELECT * FROM meeting " +
            "WHERE owner_id = ?1 " +
            "AND creation_state_id = (SELECT id FROM creation_state WHERE creation_status = 'IN_PROGRESS')", nativeQuery = true)
    Optional<Meeting> findByCreationStatusEqualsInProgress(Integer ownerId);
}
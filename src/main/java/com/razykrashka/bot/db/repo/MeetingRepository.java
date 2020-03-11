package com.razykrashka.bot.db.repo;


import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {

    List<Meeting> findAllByTelegramUser(TelegramUser telegramUser);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM meeting WHERE id = ?1", nativeQuery = true)
    void deleteMeetingById(Integer meetingId);

    List<Meeting> findAllByCreationStatusEqualsAndTelegramUser(CreationStatus creationStatus, TelegramUser telegramUser);

    Optional<Meeting> findTop1ByCreationStatusEqualsAndTelegramUser(CreationStatus creationStatus, TelegramUser telegramUser);
}

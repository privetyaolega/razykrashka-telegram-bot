package com.razykrashka.bot.db.repo;


import com.razykrashka.bot.entity.Meeting;
import com.razykrashka.bot.entity.TelegramUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {
    List<Meeting> findAllByTelegramUserTelegramId(Integer telegramId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_meeting WHERE meeting_id = ?1", nativeQuery = true)
    void deleteMeeting(Integer meetingId);
    List<Meeting> findAllByTelegramUser(TelegramUser user);

    List<Meeting> findAllByCreationStatusEqualsAndTelegramUser(CreationStatus creationStatus, TelegramUser telegramUser);
}

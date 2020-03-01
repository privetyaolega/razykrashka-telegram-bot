package com.razykrashka.bot.repository;


import com.razykrashka.bot.entity.Meeting;
import com.razykrashka.bot.entity.TelegramUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
}

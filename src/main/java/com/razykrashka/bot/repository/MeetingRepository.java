package com.razykrashka.bot.repository;


import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {
    List<Meeting> findAllByTelegramUser(TelegramUser user);

    List<Meeting> findAllByCreationStatusEqualsAndTelegramUser(CreationStatus creationStatus, TelegramUser telegramUser);
}

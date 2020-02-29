package com.razykrashka.bot.db.repo;


import com.razykrashka.bot.db.entity.Meeting;
import com.razykrashka.bot.db.entity.TelegramUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {
    List<Meeting> findAllByTelegramUser(TelegramUser user);
}
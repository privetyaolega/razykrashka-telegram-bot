package com.razykrashka.bot.repository;


import com.razykrashka.bot.entity.Meeting;
import com.razykrashka.bot.entity.TelegramUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {
    List<Meeting> findAllByTelegramUser(TelegramUser user);
}

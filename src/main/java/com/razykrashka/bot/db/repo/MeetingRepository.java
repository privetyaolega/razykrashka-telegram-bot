package com.razykrashka.bot.db.repo;


import com.razykrashka.bot.db.entity.Meeting;
import org.springframework.data.repository.CrudRepository;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {

}

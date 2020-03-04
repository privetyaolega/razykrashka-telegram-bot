package com.razykrashka.bot.repository;

import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import org.springframework.data.repository.CrudRepository;

public interface MeetingInfoRepository extends CrudRepository<MeetingInfo, Integer> {

}

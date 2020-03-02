package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingInfoRepository extends CrudRepository<MeetingInfo, Integer> {

    List<MeetingInfo> findAllByParticipantLimitEquals(Integer participantLimit);

}

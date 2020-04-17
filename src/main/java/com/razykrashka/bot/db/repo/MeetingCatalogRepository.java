package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingCatalog;
import org.springframework.data.repository.CrudRepository;

public interface MeetingCatalogRepository extends CrudRepository<MeetingCatalog, Integer> {

}
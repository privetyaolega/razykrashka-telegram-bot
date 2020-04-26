package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.meeting.TopicCatalogue;
import org.springframework.data.repository.CrudRepository;

public interface MeetingCatalogRepository extends CrudRepository<TopicCatalogue, Integer> {

}
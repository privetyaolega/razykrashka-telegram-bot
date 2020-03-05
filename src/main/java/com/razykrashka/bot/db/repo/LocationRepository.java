package com.razykrashka.bot.repository;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location, Integer> {

}

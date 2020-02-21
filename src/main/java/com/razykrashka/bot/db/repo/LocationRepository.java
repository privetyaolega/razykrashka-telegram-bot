package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.Location;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location, Integer> {

}

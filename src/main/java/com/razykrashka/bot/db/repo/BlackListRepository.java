package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.infrastructure.BlackList;
import org.springframework.data.repository.CrudRepository;

public interface BlackListRepository extends CrudRepository<BlackList, Integer> {
}
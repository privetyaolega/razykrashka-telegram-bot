package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CreationStateRepository extends CrudRepository<CreationState, Integer> {
    Optional<CreationState> findByIdEquals(Integer id);
}
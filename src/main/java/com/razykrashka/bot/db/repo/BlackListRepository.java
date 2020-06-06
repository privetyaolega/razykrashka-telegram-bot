package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.infrastructure.BlackList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlackListRepository extends CrudRepository<BlackList, Long> {
    @Query(value = "SELECT * FROM black_list where user_id = ?1 LIMIT 1", nativeQuery = true)
    Optional<BlackList> findByUserId(Integer id);
}
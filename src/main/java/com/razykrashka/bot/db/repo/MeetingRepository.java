package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {

    @Query(value = "SELECT * FROM user_meeting " +
            "INNER JOIN meeting " +
            "ON meeting.id = user_meeting.meeting_id " +
            "where user_id = ?1", nativeQuery = true)
    List<Meeting> findAllScheduledMeetingsForUserById(Integer id);

    @Query(value = "SELECT * FROM meeting m " +
            "JOIN creation_state c " +
            "ON m.creation_state_id = c.id " +
            "JOIN user u " +
            "ON u.id = m.owner_id " +
            "WHERE u.id = ?1 " +
            "AND c.creation_status = 'IN_PROGRESS'", nativeQuery = true)
    Optional<Meeting> findByCreationStatusEqualsInProgress(Integer ownerId);

    @Query(value = "SELECT COUNT(m.id) FROM meeting m " +
            "LEFT JOIN creation_state c " +
            "ON m.creation_state_id = c.id " +
            "WHERE c.creation_status = 'DONE' " +
            "AND meeting_date_time < NOW()", nativeQuery = true)
    Long countByMeetingDateTimeBefore();
}
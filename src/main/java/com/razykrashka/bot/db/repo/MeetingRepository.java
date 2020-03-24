package com.razykrashka.bot.db.repo;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {

    @Query(value = "SELECT * FROM user_meeting INNER JOIN meeting ON meeting.id = user_meeting.meeting_id where user_id = ?1", nativeQuery = true)
    List<Meeting> findAllScheduledMeetingsForUserById(Integer id);

    @Query(value = "SELECT * FROM meeting " +
            "WHERE owner_id = ?1 " +
            "AND creation_state_id = (SELECT id FROM creation_state WHERE creation_status = 'IN_PROGRESS')", nativeQuery = true)
    Optional<Meeting> findByCreationStatusEqualsInProgress(Integer ownerId);

    @Query(value = "SELECT * " +
            "FROM meeting m " +
            "INNER JOIN creation_state c " +
            "ON m.creation_state_id = c.id " +
            "AND m.meeting_date_time >= NOW() " +
            "ORDER BY m.meeting_date_time", nativeQuery = true)
    List<Meeting> findAllActiveAndDone();

    Meeting findMeetingById(int meetingId);
    @Query(value = "SELECT * " +
            "FROM meeting m " +
            "INNER JOIN creation_state c " +
            "ON m.creation_state_id = c.id " +
            "AND m.meeting_date_time < NOW() " +
            "ORDER BY m.meeting_date_time", nativeQuery = true)
    List<Meeting> findAllExpired();
}
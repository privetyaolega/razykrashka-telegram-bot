package com.razykrashka.bot.repository;


import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {

    String SELECT_ALL_MEETING_FIELDS = "SELECT meeting.id, meeting.creation_date_time, meeting.creation_status, meeting.meeting_date_time, meeting.location_id, meeting.meeting_info_id, meeting.owner_id ";
    String ALL_USER_MEETINGS = SELECT_ALL_MEETING_FIELDS
            + "FROM meeting "
            + "INNER JOIN user "
            + "ON user.id = meeting.owner_id "
            + "WHERE user.telegram_id = ?1 "
            + "UNION "
            + SELECT_ALL_MEETING_FIELDS
            + "FROM user_meeting "
            + "INNER JOIN meeting "
            + "ON user_meeting.meeting_id = meeting.id "
            + "WHERE user_meeting.user_id = "
            + "(SELECT user.id FROM user WHERE user.telegram_id = ?1) ";


    @Query(value = ALL_USER_MEETINGS, nativeQuery = true)
    Optional<List<Meeting>> findAllMeetings(Integer telegramId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_meeting WHERE meeting_id = ?1", nativeQuery = true)
    void deleteMeeting(Integer meetingId);

    List<Meeting> findAllByCreationStatusEqualsAndTelegramUser(CreationStatus creationStatus, TelegramUser telegramUser);
}

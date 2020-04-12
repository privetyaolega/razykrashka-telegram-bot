package com.razykrashka.bot.db.service;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import com.razykrashka.bot.db.repo.MeetingRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeetingService {

    MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Stream<Meeting> getAllMeetings() {
        return StreamSupport.stream(meetingRepository.findAll().spliterator(), false);
    }

    public List<Meeting> getAllCreationStatusDone() {
        return getAllMeetings()
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public List<Meeting> getAllExpired() {
        return getAllMeetings()
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public List<Meeting> getAllMeetingDateToday() {
        return getAllMeetings()
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().toLocalDate().isEqual(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public Stream<Meeting> getAllActive() {
        return getAllMeetings()
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().isAfter(LocalDateTime.now()));
    }

    public List<Meeting> getAllActiveOffline() {
        return getAllCreationStatusDone().stream()
                .filter(m -> m.getFormat().equals(MeetingFormatEnum.OFFLINE))
                .collect(Collectors.toList());
    }
}
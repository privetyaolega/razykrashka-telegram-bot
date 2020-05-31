package com.razykrashka.bot.db.service;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.repo.MeetingRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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

    public Stream<Meeting> getAllUpcomingMeetings() {
        return StreamSupport.stream(meetingRepository.findAllUpcomingMeetings().spliterator(), false);
    }

    public List<Meeting> getAllArchivedMeetings() {
        return getAllMeetings()
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Meeting::getMeetingDateTime).reversed())
                .collect(Collectors.toList());
    }

    public List<Meeting> getAllCreationStatusDone() {
        return getAllActive().collect(Collectors.toList());
    }

    public List<Meeting> getAllMeetingDateToday() {
        return getAllUpcomingMeetings()
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().toLocalDate().isEqual(LocalDate.now())
                        && m.getMeetingDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public Stream<Meeting> getAllActive() {
        return getAllUpcomingMeetings()
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().isAfter(LocalDateTime.now()));
    }
}
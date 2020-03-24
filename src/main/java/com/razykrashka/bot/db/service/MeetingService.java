package com.razykrashka.bot.db.service;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.repo.MeetingRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeetingService {

    MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public List<Meeting> getAllCreationStatusDone() {
        return StreamSupport.stream(meetingRepository.findAll().spliterator(), false)
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}
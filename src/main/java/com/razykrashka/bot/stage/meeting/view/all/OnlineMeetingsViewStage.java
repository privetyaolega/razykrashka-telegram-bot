package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingFormatEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class OnlineMeetingsViewStage extends PaginationMeetingsViewStage {

    @Override
    public boolean processCallBackQuery() {
        meetings = StreamSupport.stream(meetingRepository.findAll().spliterator(), false)
                .filter(m -> m.getCreationState().getCreationStatus().equals(CreationStatus.DONE)
                        && m.getMeetingDateTime().isAfter(LocalDateTime.now())
                        && m.getFormat().equals(MeetingFormatEnum.ONLINE))
                .collect(Collectors.toList());
        return super.processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ActiveMeetingsViewStage extends BaseMeetingsViewStage {

    @Override
    public boolean processCallBackQuery() {
        meetings = StreamSupport.stream(meetingRepository.findAll().spliterator(), false)
                .filter(m -> m.getMeetingDateTime().isAfter(LocalDateTime.now())
                        && m.getCreationState().getCreationStatus().equals(CreationStatus.DONE))
                .collect(Collectors.toList());
        return super.processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
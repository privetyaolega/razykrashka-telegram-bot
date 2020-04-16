package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

@Log4j2
@Component
public class MyMeetingsViewStage extends PaginationMeetingsViewStage {

    public final static String KEYWORD = "My Meetings";

    @Override
    public void handleRequest() {
        meetings = meetingRepository.findAllScheduledMeetingsForUserById(updateHelper.getTelegramUserId())
                .stream()
                .filter(m -> m.getMeetingDateTime().plusHours(1).isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Meeting::getMeetingDateTime))
                .collect(Collectors.toList());
        super.generateMainMessage(meetingMessageUtils::getPaginationAllGeneral);
    }

    @Override
    public void processCallBackQuery() {
        handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals("/my")
                || updateHelper.isMessageTextEquals(KEYWORD);
    }
}
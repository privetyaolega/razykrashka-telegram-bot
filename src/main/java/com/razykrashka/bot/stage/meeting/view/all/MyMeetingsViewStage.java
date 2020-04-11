package com.razykrashka.bot.stage.meeting.view.all;

import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Log4j2
@Component
public class MyMeetingsViewStage extends PaginationMeetingsViewStage {

    public MyMeetingsViewStage() {
        stageInfo = StageInfo.MY_MEETING_VIEW;
    }

    @Override
    public void handleRequest() {
        meetings = meetingRepository.findAllScheduledMeetingsForUserById(updateHelper.getUser().getId())
                .stream()
                .filter(m -> m.getMeetingDateTime().minusHours(1).isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        super.processCallBackQuery();
    }

    @Override
    public boolean processCallBackQuery() {
        handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(this.getStageInfo().getKeyword());
    }
}
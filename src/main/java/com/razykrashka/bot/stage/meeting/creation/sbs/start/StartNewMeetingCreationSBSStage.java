package com.razykrashka.bot.stage.meeting.creation.sbs.start;

import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.input.DateMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.loading.LoadingThreadV2;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartNewMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public boolean processCallBackQuery() {
        LoadingThreadV2 loadingThread = startLoadingThread();
        meetingRepository.findByCreationStatusEqualsInProgress(updateHelper.getUser().getId())
                .ifPresent(m -> meetingRepository.delete(m));
        super.setActiveNextStage(DateMeetingCreationSBSStage.class);
        joinToThread(loadingThread);
        razykrashkaBot.getContext().getBean(DateMeetingCreationSBSStage.class).handleRequest();
        return true;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}
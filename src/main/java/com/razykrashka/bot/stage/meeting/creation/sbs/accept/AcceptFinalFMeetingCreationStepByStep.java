package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.loading.LoadingThread;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Log4j2
@Component
public class AcceptFinalFMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        LoadingThread loadingThread = startLoadingThread();

        Meeting meeting = super.getMeetingInCreation();
        meeting.setCreationDateTime(LocalDateTime.now());
        meeting.setCreationStatus(CreationStatus.DONE);
        meeting.getParticipants().add(razykrashkaBot.getUser());
        meetingRepository.save(meeting);
        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messageManager.updateMessage("MEETING CREATED")
                .sendSticker("success2.tgs");
    }

    @Override
    public boolean isStageActive() {
        return super.getStageActivity() && updateHelper.isCallBackDataContains();
    }
}
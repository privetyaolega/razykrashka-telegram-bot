package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.loading.LoadingThread;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

import java.io.File;
import java.time.LocalDateTime;

@Log4j2
@Component
public class AcceptFinalFMeetingCreationStepByStep extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        LoadingThread thread = new LoadingThread();
        razykrashkaBot.getContext().getAutowireCapableBeanFactory().autowireBean(thread);
        thread.start();

        Meeting meeting = super.getMeetingInCreation();
        meeting.setCreationDateTime(LocalDateTime.now());
        meeting.setCreationStatus(CreationStatus.DONE);
        meetingRepository.save(meeting);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messageManager.updateMessage("MEETING CREATED");
        razykrashkaBot.sendSticker(new SendSticker()
                .setSticker(new File("src/main/resources/stickers/successMeetingCreationSticker.tgs")));
    }

    @Override
    public boolean isStageActive() {
        return super.getStageActivity() && updateHelper.isCallBackDataContains();
    }
}
package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
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
        Meeting meeting = super.getMeetingInCreation();
        meeting.setCreationDateTime(LocalDateTime.now());
        meeting.setCreationStatus(CreationStatus.DONE);
        meetingRepository.save(meeting);

        messageManager.updateMessage("MEETING CREATED");
        razykrashkaBot.sendSticker(new SendSticker()
                .setSticker(new File("src/main/resources/stickers/successMeetingCreationSticker.tgs")));
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getCallbackQuery() == null) {
            return false;
        } else {
            return super.getStageActivity() && razykrashkaBot.getRealUpdate().getCallbackQuery()
                    .equals(this.getClass().getSimpleName());
        }
    }
}
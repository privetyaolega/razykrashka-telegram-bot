package com.razykrashka.bot.stage.meeting.creation.sbs.accept;

import com.razykrashka.bot.db.entity.razykrashka.Meeting;
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
        Meeting meeting = super.getMeeting();
        meeting.setCreationDateTime(LocalDateTime.now());
        meeting.getMeetingInfo().setQuestions("");
        locationRepository.save(meeting.getLocation());
        meetingInfoRepository.save(meeting.getMeetingInfo());
        meetingRepository.save(meeting);

        razykrashkaBot.getUser().getToGoMeetings().add(meeting);
        razykrashkaBot.getUser().getCreatedMeetings().add(meeting);
        telegramUserRepository.save(razykrashkaBot.getUser());

        super.setMeeting(null);

        messageSender.sendSimpleTextMessage("MEETING CREATED");
        razykrashkaBot.sendSticker(new SendSticker().setSticker(new File("src/main/resources/stickers/successMeetingCreationSticker.tgs")));
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
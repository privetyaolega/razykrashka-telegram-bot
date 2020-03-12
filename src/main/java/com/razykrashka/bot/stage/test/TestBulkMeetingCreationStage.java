package com.razykrashka.bot.stage.test;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
import com.razykrashka.bot.db.entity.razykrashka.meeting.SpeakingLevel;
import com.razykrashka.bot.exception.YandexMapApiException;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.creation.CreateMeetingByTemplateStage;
import com.razykrashka.bot.ui.helpers.MapLocationHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;

@Log4j2
@Component
public class TestBulkMeetingCreationStage extends MainStage {

    @Autowired
    MapLocationHelper mapLocationHelper;

    private Meeting meeting;

    public TestBulkMeetingCreationStage() {
        stageInfo = StageInfo.TEST_BULK_MEETING_CREATION;
    }

    @Override
    public void handleRequest() {
        int meetingsAmount = Integer.parseInt(updateHelper.getMessageText()
                .replace(this.stageInfo.getKeyword(), ""));
        try {
            for (int i = 0; i < meetingsAmount; i++) {
                MeetingInfo meetingInfo = MeetingInfo.builder()
                        .questions("Test Questions")
                        .topic("Test Topic")
                        .speakingLevel(SpeakingLevel.ADVANCED)
                        .build();
                meetingInfoRepository.save(meetingInfo);

                Location location = null;
                try {
                    location = mapLocationHelper.getLocation("Кальварийская 46");
                } catch (YandexMapApiException e) {
                    e.printStackTrace();
                }
                locationRepository.save(location);

                meeting = Meeting.builder()
                        .telegramUser(razykrashkaBot.getUser())
                        .meetingDateTime(LocalDateTime.now())
                        .creationDateTime(LocalDateTime.now())
                        .meetingInfo(meetingInfo)
                        .location(location)
                        .creationStatus(CreationStatus.DONE)
                        .telegramUser(razykrashkaBot.getUser())
                        .participants(new HashSet<>())
                        .build();

                meeting.getParticipants().add(razykrashkaBot.getUser());
                meetingRepository.save(meeting);
            }
        } catch (Exception e) {
            messageManager.sendSimpleTextMessage("SOMETHING WENT WRONG DURING MEETING CREATION")
                    .sendSticker("failSticker.png");
            razykrashkaBot.getContext().getBean(CreateMeetingByTemplateStage.class).handleRequest();
        }

        messageManager.sendSimpleTextMessage("MEETING CREATED")
                .sendSticker("successMeetingCreationSticker.tgs");
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageContains(this.getStageInfo().getKeyword());
    }
}
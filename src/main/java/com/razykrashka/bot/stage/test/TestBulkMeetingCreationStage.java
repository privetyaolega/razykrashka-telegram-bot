package com.razykrashka.bot.stage.test;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.*;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.exception.YandexMapApiException;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.stage.meeting.creation.CreateMeetingByTemplateStage;
import com.razykrashka.bot.ui.helpers.LocationHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;

@Log4j2
@Component
public class TestBulkMeetingCreationStage extends MainStage {

    @Autowired
    LocationHelper locationHelper;
    @Autowired
    protected CreationStateRepository creationStateRepository;

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
                    location = locationHelper.getLocation("Кальварийская 46");
                } catch (YandexMapApiException e) {
                    e.printStackTrace();
                }
                locationRepository.save(location);

                CreationState creationState = CreationState.builder()
                        .creationStatus(CreationStatus.DONE)
                        .build();
                creationStateRepository.save(creationState);

                meeting = Meeting.builder()
                        .telegramUser(updateHelper.getUser())
                        .meetingDateTime(LocalDateTime.now().plusDays(5))
                        .creationDateTime(LocalDateTime.now())
                        .meetingInfo(meetingInfo)
                        .location(location)
                        .creationState(creationState)
                        .telegramUser(updateHelper.getUser())
                        .participants(new HashSet<>())
                        .build();

                meeting.getParticipants().add(updateHelper.getUser());
                meetingRepository.save(meeting);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
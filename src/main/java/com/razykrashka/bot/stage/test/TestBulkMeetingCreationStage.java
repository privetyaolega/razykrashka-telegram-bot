package com.razykrashka.bot.stage.test;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationState;
import com.razykrashka.bot.db.entity.razykrashka.meeting.CreationStatus;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.db.entity.razykrashka.meeting.MeetingInfo;
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
import java.util.List;
import java.util.Random;

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
                List<MeetingInfo> meetingInfoList = meetingInfoRepository.findAllByParticipantLimitEquals(0);
                MeetingInfo randomMeetingInfo = meetingInfoList.get(new Random().nextInt(meetingInfoList.size()));

                MeetingInfo mi = MeetingInfo.builder()
                        .topic(randomMeetingInfo.getTopic())
                        .questions(randomMeetingInfo.getQuestions())
                        .speakingLevel(randomMeetingInfo.getSpeakingLevel())
                        .participantLimit(new Random().nextInt(5) + 2)
                        .build();
                meetingInfoRepository.save(mi);

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
                        .meetingInfo(mi)
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
                .sendRandomSticker("success");
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageContains(this.getStageInfo().getKeyword());
    }
}
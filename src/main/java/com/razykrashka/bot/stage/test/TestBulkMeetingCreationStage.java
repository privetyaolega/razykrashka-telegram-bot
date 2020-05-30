package com.razykrashka.bot.stage.test;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.*;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.db.repo.LocationRepository;
import com.razykrashka.bot.db.repo.MeetingCatalogRepository;
import com.razykrashka.bot.exception.YandexMapApiException;
import com.razykrashka.bot.integration.discord.DiscordBot;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.ui.helpers.LocationHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class TestBulkMeetingCreationStage extends MainStage {

    @Autowired
    LocationHelper locationHelper;
    @Autowired
    protected LocationRepository locationRepository;
    @Autowired
    protected CreationStateRepository creationStateRepository;
    @Autowired
    MeetingCatalogRepository meetingCatalogRepository;
    @Autowired
    DiscordBot discordBot;

    private Meeting meeting;

    @Override
    public void handleRequest() {
        int meetingsAmount = Integer.parseInt(updateHelper.getMessageText()
                .replace("/cm", ""));
        try {
            for (int i = 0; i < meetingsAmount; i++) {
                List<TopicCatalogue> topicCatalogueList = StreamSupport
                        .stream(meetingCatalogRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
                TopicCatalogue randomMeetingInfo = topicCatalogueList.get(new Random().nextInt(topicCatalogueList.size()));

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
                        .format(MeetingFormatEnum.OFFLINE)
                        .location(location)
                        .creationState(creationState)
                        .telegramUser(updateHelper.getUser())
                        .participants(new HashSet<>())
                        .build();

                meeting.getParticipants().add(updateHelper.getUser());
                meetingRepository.save(meeting);

                messageManager.sendSimpleTextMessage(discordBot.createVoiceMeetingChannel(meeting));
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageManager.sendSimpleTextMessage("SOMETHING WENT WRONG DURING MEETING CREATION")
                    .sendSticker("failSticker.png");
        }

        messageManager.sendSimpleTextMessage("MEETING CREATED")
                .sendRandomSticker("success");
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageContains("/cm");
    }
}
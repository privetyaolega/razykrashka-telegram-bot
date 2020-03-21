package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.*;
import com.razykrashka.bot.db.repo.CreationStateRepository;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import com.razykrashka.bot.ui.helpers.LocationHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class NewMeetingCreationStage extends MainStage {

    @Autowired
    LocationHelper locationHelper;
    @Autowired
    protected CreationStateRepository creationStateRepository;

    private String message;
    private Meeting meetingModel;

    public NewMeetingCreationStage() {
        stageInfo = StageInfo.NEW_MEETING_CREATION;
    }

    @Override
    public void handleRequest() {
        message = updateHelper.getMessageText().replace("@Test7313494Bot", "").trim();
        try {
            Map<String, String> meetingMap = Arrays.stream(message.split("\\n\\n")).skip(1)
                    .map(x -> x.replace("\n", ""))
                    .collect(Collectors.toMap((line) -> line.split(":")[0].trim(), (x) -> x.split(":")[1].trim()));

            MeetingInfo meetingInfo = MeetingInfo.builder()
                    .questions(meetingMap.get("QUESTIONS"))
                    .topic(meetingMap.get("TOPIC"))
                    .speakingLevel(SpeakingLevel.ADVANCED)
                    .build();
            meetingInfoRepository.save(meetingInfo);

            Location location = locationHelper.getLocation(meetingMap.get("LOCATION"));
            locationRepository.save(location);

            CreationState creationState = CreationState.builder()
                    .creationStatus(CreationStatus.DONE)
                    .build();
            creationStateRepository.save(creationState);

            meetingModel = Meeting.builder()
                    .telegramUser(updateHelper.getUser())
                    .meetingDateTime(LocalDateTime.parse(meetingMap.get("DATE").trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm")))
                    .creationDateTime(LocalDateTime.now())
                    .meetingInfo(meetingInfo)
                    .location(location)
                    .creationState(creationState)
                    .build();

            meetingRepository.save(meetingModel);

            updateHelper.getUser().setPhoneNumber(meetingMap.get("CONTACT NUMBER"));
            updateHelper.getUser().getToGoMeetings().add(meetingModel);
            updateHelper.getUser().getCreatedMeetings().add(meetingModel);
            telegramUserRepository.save(updateHelper.getUser());

            messageManager.sendSimpleTextMessage("MEETING CREATED")
                    .sendSticker("successMeetingCreationSticker.tgs");
        } catch (Exception e) {
            e.printStackTrace();
            messageManager.sendSimpleTextMessage("SOMETHING WENT WROND DURING MEETING CREATION")
                    .sendSticker("failSticker.png");
            razykrashkaBot.getContext().getBean(CreateMeetingByTemplateStage.class).handleRequest();
        }
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageContains(this.getStageInfo().getKeyword());
    }
}
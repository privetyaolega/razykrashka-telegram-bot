package com.razykrashka.bot.stage.meeting.creation;

import com.razykrashka.bot.api.LoсationiqApi;
import com.razykrashka.bot.api.model.locationiq.Locationiq;
import com.razykrashka.bot.db.entity.*;
import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class NewMeetingCreationStage extends MainStage {

    private String message;
    private Meeting meetingModel;

    public NewMeetingCreationStage() {
        stageInfo = StageInfo.NEW_MEETING_CREATION;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }


    @Override
    public void handleRequest() {
        message = razykrashkaBot.getUpdate().getMessage().getText().replace("@Test7313494Bot", "").trim();
        try {
            Map<String, String> meetingMap = Arrays.stream(message.split("\\n\\n")).skip(1)
                    .map(x -> x.replace("\n", ""))
                    .collect(Collectors.toMap((line) -> line.split(":")[0].trim(), (x) -> x.split(":")[1].trim()));

            Locationiq getModel = LoсationiqApi.getLocationModel(meetingMap.get("LOCATION")).stream()
                    .filter(x -> x.getDisplayName().contains("Minsk"))
                    .findFirst().get();



            MeetingInfo meetingInfo = MeetingInfo.builder()
                    .questions(meetingMap.get("QUESTIONS"))
                    .topic(meetingMap.get("TOPIC"))
                    .speakingLevel(SpeakingLevel.ADVANCED)
                    .build();
            meetingInfoRepository.save(meetingInfo);

            Location location = Location.builder()
                    .address(meetingMap.get("LOCATION"))
                    .latitude(Float.parseFloat(getModel.getLat()))
                    .longitude(Float.parseFloat(getModel.getLon()))
                    .name(getModel.getDisplayName())
                    .locationLink(TelegramLinkEmbedded.builder()
                            .link("http://google.com")
                            .textLink(meetingMap.get("LOCATION"))
                            .build())
                    .build();
            locationRepository.save(location);

            meetingModel = Meeting.builder()
                    .telegramUser(razykrashkaBot.getUser())
                    .meetingDateTime(LocalDateTime.parse(meetingMap.get("DATE").trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm")))
                    .creationDateTime(LocalDateTime.now())
                    .meetingInfo(meetingInfo)
                    .location(location)
                    .build();
            meetingRepository.save(meetingModel);

            razykrashkaBot.getUser().setPhoneNumber(meetingMap.get("CONTACT NUMBER"));
            razykrashkaBot.getUser().getToGoMeetings().add(meetingModel);
            razykrashkaBot.getUser().getCreatedMeetings().add(meetingModel);
            telegramUserRepository1.save(razykrashkaBot.getUser());

            messageSender.sendSimpleTextMessage("MEETING CREATED");
            razykrashkaBot.sendSticker(new SendSticker().setSticker(new File("src/main/resources/stickers/successMeetingCreationSticker.tgs")));
        } catch (Exception e) {
            e.printStackTrace();
            messageSender.sendSimpleTextMessage("SOMETHING WENT WROND DURING MEETING CREATION");
            razykrashkaBot.sendSticker(new SendSticker().setSticker(new File("src/main/resources/stickers/failSticker.png")));
            razykrashkaBot.getContext().getBean(CreateMeetingByTemplateStage.class).handleRequest();
        }
    }

    @Override
    public boolean isStageActive() {
        return razykrashkaBot.getUpdate().getMessage().getText().contains(stageInfo.getKeyword());
    }
}

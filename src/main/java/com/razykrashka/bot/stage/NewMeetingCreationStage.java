package com.razykrashka.bot.stage;

import com.razykrashka.bot.api.LoсationiqApi;
import com.razykrashka.bot.api.model.locationiq.Locationiq;
import com.razykrashka.bot.db.entity.*;
import com.razykrashka.bot.db.repo.LocationRepository;
import com.razykrashka.bot.db.repo.MeetingInfoRepository;
import com.razykrashka.bot.db.repo.MeetingRepository;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class NewMeetingCreationStage extends MainStage {

    private String message;
    private Meeting meetingModel;

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private TelegramUserRepository telegramUserRepository1;
    @Autowired
    private MeetingInfoRepository meetingInfoRepository;
    @Autowired
    private LocationRepository locationRepository;

    public NewMeetingCreationStage() {
        stageInfo = StageInfo.NEW_MEETING_CREATION;
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("All Meeting"));
        keyboardFirstRow.add(new KeyboardButton("Template"));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("Information :P"));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }


    @Override
    public void handleRequest() {
        message = razykrashkaBot.getUpdate().getMessage().getText();
        try {
            Map<String, String> meetingMap = Arrays.stream(message.split("\\n\\n")).skip(1)
                    .map(x -> x.replace("\n", ""))
                    .collect(Collectors.toMap((line) -> line.split(":")[0].trim(), (x) -> x.split(":")[1].trim()));

            Locationiq getModel = LoсationiqApi.getLocationModel(meetingMap.get("LOCATION")).stream()
                    .filter(x -> x.getDisplayName().contains("Minsk"))
                    .findFirst().get();

            TelegramUser user = TelegramUser.builder()
                    .lastName(telegramUpdate.getMessage().getFrom().getLastName())
                    .firstName(telegramUpdate.getMessage().getFrom().getFirstName())
                    .userName(telegramUpdate.getMessage().getFrom().getUserName())
                    .phoneNumber(meetingMap.get("CONTACT NUMBER"))
                    .build();
            telegramUserRepository1.save(user);

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
                    .build();
            locationRepository.save(location);


            meetingModel = Meeting.builder()
                    .owner(user)
                    .meetingDateTime(LocalDateTime.parse(meetingMap.get("DATE").trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm")))
                    .creationDateTime(LocalDateTime.now())
                    .meetingInfo(meetingInfo)
                    .location(location)
                    .build();
            meetingRepository.save(meetingModel);


//            SendVenue sendVenue = new SendVenue()
//                    .setLongitude(Float.parseFloat(geoModel.getLon()))
//                    .setLatitude(Float.parseFloat(geoModel.getLat()))
//                    .setAddress(meetingModel.getLocation())
//                    .setTitle(meetingModel.getMeetingDate().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH)));
//            meetingModel.setSendVenue(sendVenue);
//
//            SendContact sendContact = new SendContact()
//                    .setPhoneNumber()
//                    .setFirstName(meetingModel.getOwner().getFirstName())
//                    .setLastName(meetingModel.getOwner().getLastName());
//            meetingModel.setSendContact(sendContact);

            razykrashkaBot.sendSimpleTextMessage("MEETING CREATED");
            razykrashkaBot.sendSticker(new SendSticker().setSticker(new File("C:\\Development\\Projects\\git-hub\\telegram_bot\\src\\main\\java\\com\\avp256\\avp256_bot\\repository\\files\\successMeetingCreationSticker.tgs")));
        } catch (Exception e) {
            razykrashkaBot.sendSimpleTextMessage("SOMETHING WENT WROND DURING MEETING CREATION");
            razykrashkaBot.sendSticker(new SendSticker().setSticker(new File("C:\\Development\\Projects\\git-hub\\telegram_bot\\src\\main\\java\\com\\avp256\\avp256_bot\\repository\\files\\failSticker.png")));
            razykrashkaBot.getContext().getBean(IntroCreateMeetingStage.class).handleRequest();
        }
    }

    @Override
    public boolean isStageActive() {
        return razykrashkaBot.getUpdate().getMessage().getText().startsWith(stageInfo.getKeyword());
    }
}

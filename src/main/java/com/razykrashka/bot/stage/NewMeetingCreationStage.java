package com.razykrashka.bot.stage;

import com.razykrashka.bot.api.model.locationiq.Locationiq;
import com.razykrashka.bot.db.entity.Meeting;
import com.razykrashka.bot.model.helper.geolocation.GeolocationHelper;
import com.razykrashka.bot.model.razykrashka.MeetingModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        message = avp256Bot.getUpdate().getMessage().getText();
        try {
            Map<String, String> meetingMap = Arrays.stream(message.split("\\n\\n")).skip(1)
                    .map(x -> x.replace("\n", ""))
                    .collect(Collectors.toMap((line) -> line.split(":")[0].trim(), (x) -> x.split(":")[1].trim()));

            meetingModel = Meeting.builder()
                    .location(meetingMap.get("LOCATION"))
                    .owner(avp256Bot.getUpdate().getMessage().getFrom())
                    .meetingDate(LocalDateTime.parse(meetingMap.get("DATE").trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm")))
                    .creationDate(LocalDateTime.now())
                    .speakingLevel(meetingMap.get("SPEAKING LEVEL"))
                    .topic(meetingMap.get("TOPIC"))
                    .questions(meetingMap.get("QUESTIONS"))
                    .build();

            Locationiq geoModel = GeolocationHelper.getGeolocationByAddress(meetingModel.getLocation())
                    .stream().filter(model -> model.getDisplayName().contains("Minsk")).findFirst().get();

            SendVenue sendVenue = new SendVenue()
                    .setLongitude(Float.parseFloat(geoModel.getLon()))
                    .setLatitude(Float.parseFloat(geoModel.getLat()))
                    .setAddress(meetingModel.getLocation())
                    .setTitle(meetingModel.getMeetingDate().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm", Locale.ENGLISH)));
            meetingModel.setSendVenue(sendVenue);

            SendContact sendContact = new SendContact()
                    .setPhoneNumber(meetingMap.get("CONTACT NUMBER"))
                    .setFirstName(meetingModel.getOwner().getFirstName())
                    .setLastName(meetingModel.getOwner().getLastName());
            meetingModel.setSendContact(sendContact);

            gsonHelper.writeToFile(meetingModel);
            avp256Bot.sendSimpleTextMessage("MEETING CREATED");
            avp256Bot.sendSticker(new SendSticker().setSticker(new File("C:\\Development\\Projects\\git-hub\\telegram_bot\\src\\main\\java\\com\\avp256\\avp256_bot\\repository\\files\\successMeetingCreationSticker.tgs")));
        } catch (Exception e) {
            avp256Bot.sendSimpleTextMessage("SOMETHING WENT WROND DURING MEETING CREATION");
            avp256Bot.sendSticker(new SendSticker().setSticker(new File("C:\\Development\\Projects\\git-hub\\telegram_bot\\src\\main\\java\\com\\avp256\\avp256_bot\\repository\\files\\failSticker.png")));
            avp256Bot.getContext().getBean(IntroCreateMeetingStage.class).handleRequest();
        }
    }

    @Override
    public boolean isStageActive() {
        return avp256Bot.getUpdate().getMessage().getText().startsWith(stageInfo.getKeyword());
    }
}

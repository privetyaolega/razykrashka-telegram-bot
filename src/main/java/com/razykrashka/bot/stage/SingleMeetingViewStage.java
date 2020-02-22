package com.razykrashka.bot.stage;

import com.razykrashka.bot.db.entity.Meeting;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class SingleMeetingViewStage extends MainStage {

    private Meeting meeting;

    public SingleMeetingViewStage() {
        stageInfo = StageInfo.SINGLE_MEETING_VIEW;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }

    @Override
    public void handleRequest() {
        Integer id = Integer.valueOf(razykrashkaBot.getUpdate().getMessage().getText()
                .replace(this.getStageInfo().getKeyword(), ""));
        meeting = meetingRepository.findById(id).get();

        String messageText = "<code>MEETING # " + meeting.getId() + "</code>\n" +
                meeting.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm",
                        Locale.ENGLISH)) + "\n"
                + meeting.getLocation().getLocationLink().toString() + "\n"
                + meeting.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                + meeting.getMeetingInfo().getQuestions() + "\n"
                + meeting.getMeetingInfo().getTopic() + "\n";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(razykrashkaBot.getUpdate().getMessage().getChat().getId());
        sendMessage.setParseMode("html");
        sendMessage.setText(messageText);

        sendMessage.setReplyMarkup(getKeyboard(meeting));
        razykrashkaBot.executeBot(sendMessage);

    }

    @Override
    public ReplyKeyboard getKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Information").setCallbackData(stageInfo.getStageName() + "_information"));

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList();
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Contact").setCallbackData(stageInfo.getStageName() + "_contact"));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Map").setCallbackData(stageInfo.getStageName() + "_map"));

        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1, keyboardButtonsRow2));
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard getKeyboard(Meeting model) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

//                .setCallbackData(stageInfo.getStageName() + "_information" + model.getId()));

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList();
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Contact").setCallbackData(stageInfo.getStageName() + "_contact" + meeting.getId()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Map").setCallbackData(stageInfo.getStageName() + "_map" + meeting.getId()));
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow2));
        return inlineKeyboardMarkup;
    }


    @Override
    public boolean processCallBackQuery() {
        String callBackData = razykrashkaBot.getCallbackQuery().getData();

        meeting = StreamSupport.stream(meetingRepository.findAll().spliterator(), false).
                filter(x -> callBackData.contains(String.valueOf(x.getId()))).findFirst().get();

        if (callBackData.equals(stageInfo.getStageName() + "_contact" + meeting.getId())) {
            razykrashkaBot.sendContact(new SendContact().setLastName(meeting.getOwner().getLastName())
                    .setFirstName(meeting.getOwner().getFirstName())
                    .setPhoneNumber(meeting.getOwner().getPhoneNumber()));
        }
        if (callBackData.equals(stageInfo.getStageName() + "_map" + meeting.getId())) {
            razykrashkaBot.sendVenue(new SendVenue().setTitle("TEST TITLE")
                    .setLatitude(meeting.getLocation().getLatitude())
                    .setLongitude(meeting.getLocation().getLongitude())
                    .setAddress("TEST ADDRESS"));
        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return razykrashkaBot.getUpdate().getMessage().getText().startsWith(stageInfo.getKeyword());
    }
}

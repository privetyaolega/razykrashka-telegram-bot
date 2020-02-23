package com.razykrashka.bot.service.stage;

import com.razykrashka.bot.entity.Meeting;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class AllMeetingViewStage extends MainStage {

    List<Meeting> modelList = new ArrayList<>();

    public AllMeetingViewStage() {
        stageInfo = StageInfo.ALL_MEETING_VIEW;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }

    @Override
    public void handleRequest() {
        modelList = StreamSupport.stream(meetingRepository.findAll().spliterator(), false).collect(Collectors.toList());
        if (modelList.size() == 0) {
            razykrashkaBot.sendSimpleTextMessage("NO MEETINGS :(");
        } else {
            String messageText = modelList.stream().skip(0).limit(5)
                    .map(model -> model.getMeetingDateTime().format(DateTimeFormatter.ofPattern("dd MMMM (EEEE) HH:mm",
                            Locale.ENGLISH)) + "\n"
                            + "\uD83D\uDCCD" + model.getLocation().getLocationLink().toString() + "\n"
                            + model.getMeetingInfo().getSpeakingLevel().toString() + "\n"
                            + model.getMeetingInfo().getTopic() + "\n"
                            + "INFORMATION: /meeting" + model.getId())
                    .collect(Collectors.joining("\n\n", "\uD83D\uDCAB Найдено " + modelList.size() + " встреч(и)\n\n", ""));

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(razykrashkaBot.getUpdate().getMessage().getChat().getId());
            sendMessage.setParseMode("html");
            sendMessage.setText(messageText);
            if (modelList.size() > 5) {
                // PAGINATION INLINE KEYBOARD
                sendMessage.setReplyMarkup(getKeyboard(null));
            }
            razykrashkaBot.executeBot(sendMessage);
        }
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
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Information")
                .setSwitchInlineQueryCurrentChat("TEST MESSSAGE"));

//                .setCallbackData(stageInfo.getStageName() + "_information" + model.getId()));

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList();
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Contact").setCallbackData(stageInfo.getStageName() + "_contact"));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Map").setCallbackData(stageInfo.getStageName() + "_map"));
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1, keyboardButtonsRow2));
        return inlineKeyboardMarkup;
    }


    @Override
    public boolean processCallBackQuery() {
        String callBackData = razykrashkaBot.getCallbackQuery().getData();
        Meeting meetingModel = modelList.stream().filter(x -> callBackData.contains(String.valueOf(x.getId()))).findFirst().get();

        if (callBackData.equals(stageInfo.getStageName() + "_contact" + meetingModel.getId())) {
            razykrashkaBot.sendContact(new SendContact().setLastName(meetingModel.getTelegramUser().getLastName())
                    .setFirstName(meetingModel.getTelegramUser().getFirstName())
                    .setPhoneNumber(meetingModel.getTelegramUser().getPhoneNumber()));
        }
        if (callBackData.equals(stageInfo.getStageName() + "_map" + meetingModel.getId())) {
            razykrashkaBot.sendVenue(new SendVenue().setTitle("TEST TITLE")
                    .setLatitude(meetingModel.getLocation().getLatitude())
                    .setLongitude(meetingModel.getLocation().getLongitude())
                    .setAddress("TEST ADDRESS"));
        }
        if (callBackData.equals(stageInfo.getStageName() + "_information" + meetingModel.getId())) {
            String message = meetingModel.getMeetingInfo().getQuestions() + "\\n" + meetingModel.getMeetingInfo().getTopic() + "\\n" + meetingModel.getId();
            razykrashkaBot.updateMessage(message, (InlineKeyboardMarkup) getKeyboard(meetingModel));
        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return razykrashkaBot.getUpdate().getMessage().getText().startsWith(stageInfo.getKeyword());
    }
}

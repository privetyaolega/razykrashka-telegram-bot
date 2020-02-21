package com.razykrashka.bot.stage;

import com.razykrashka.bot.db.entity.Meeting;
import com.razykrashka.bot.db.repo.MeetingRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class ViewExistingMeetingsStage extends MainStage {

    @Autowired
    private MeetingRepository meetingRepository;
    List<Meeting> modelList;

    public ViewExistingMeetingsStage() {
        stageInfo = StageInfo.VIEW_EXISTING_MEETINGS;
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
            modelList.forEach(model -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(razykrashkaBot.getUpdate().getMessage().getChat().getId());
                sendMessage.setText(model.getMeetingInfo().getTopic() + " " + model.getMeetingInfo().getSpeakingLevel());
                sendMessage.setReplyMarkup(getKeyboard(model));
                razykrashkaBot.executeBot(sendMessage);
            });
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
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Contact").setCallbackData(stageInfo.getStageName() + "_contact" + model.getId()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Map").setCallbackData(stageInfo.getStageName() + "_map" + model.getId()));
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1, keyboardButtonsRow2));
        return inlineKeyboardMarkup;
    }


    @Override
    public boolean processCallBackQuery() {
        String callBackData = razykrashkaBot.getUpdate().getCallbackQuery().getData();
        Meeting meetingModel = modelList.stream().filter(x -> callBackData.contains(String.valueOf(x.getId()))).findFirst().get();

        if (callBackData.equals(stageInfo.getStageName() + "_contact" + meetingModel.getId())) {
            razykrashkaBot.sendContact(new SendContact().setLastName(meetingModel.getOwner().getLastName())
                    .setFirstName(meetingModel.getOwner().getFirstName())
                    .setPhoneNumber(meetingModel.getOwner().getPhoneNumber()));
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

package com.razykrashka.bot.stage;

import com.razykrashka.bot.model.razykrashka.MeetingModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class ViewExistingMeetingsStage extends MainStage {

    private String message;
    private List<MeetingModel> modelList;

    public ViewExistingMeetingsStage() {
        stageInfo = StageInfo.VIEW_EXISTING_MEETINGS;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }

    @Override
    public void handleRequest() {
        modelList = gsonHelper.readFromFile();
        if (modelList.size() == 0) {
            avp256Bot.sendSimpleTextMessage("NO MEETINGS :(");
        } else {
            modelList.forEach(model -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(avp256Bot.getUpdate().getMessage().getChat().getId());
                sendMessage.setText(model.getTopic() + " " + model.getSpeakingLevel());
                sendMessage.setReplyMarkup(getKeyboard(model));
                avp256Bot.executeBot(sendMessage);
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

    public ReplyKeyboard getKeyboard(MeetingModel model) {
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
        String callBackData = avp256Bot.getUpdate().getCallbackQuery().getData();
        MeetingModel meetingModel = modelList.stream().filter(x -> callBackData.contains(String.valueOf(x.getId()))).findFirst().get();

        if (callBackData.equals(stageInfo.getStageName() + "_contact" + meetingModel.getId())) {
            avp256Bot.sendContact(meetingModel.getSendContact());
        }
        if (callBackData.equals(stageInfo.getStageName() + "_map" + meetingModel.getId())) {
            avp256Bot.sendVenue(meetingModel.getSendVenue());
        }
        if (callBackData.equals(stageInfo.getStageName() + "_information" + meetingModel.getId())) {
            String message = meetingModel.getTopic() + "\\n" + meetingModel.getSpeakingLevel() + "\\n" + meetingModel.getQuestions();
            avp256Bot.updateMessage(message, (InlineKeyboardMarkup) getKeyboard(meetingModel));
        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return avp256Bot.getUpdate().getMessage().getText().startsWith(stageInfo.getKeyword());
    }
}

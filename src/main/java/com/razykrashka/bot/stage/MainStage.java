package com.razykrashka.bot.stage;

import com.razykrashka.bot.model.telegram.TelegramUpdate;
import com.razykrashka.bot.repository.json.GsonHelper;
import com.razykrashka.bot.service.Avp256Bot;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public abstract class MainStage implements Stage {

    @Autowired
    Avp256Bot avp256Bot;

    @Autowired
    protected GsonHelper gsonHelper;
    protected StageInfo stageInfo;
    protected boolean stageActivity;
    protected TelegramUpdate telegramUpdate;

    @Override
    public void handleRequest() {
        if (this.processCallBackQuery()) return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(telegramUpdate.getMessage().getChat().getId());
        sendMessage.setText(stageInfo.getWelcomeMessageEn());
        sendMessage.setReplyMarkup(getKeyboard());
//        if (stageInfo.getWelcomeMessageRu() != null) {
//            sendMessage.setReplyMarkup(getInlineRuEnKeyboard("en_ru", "RU \uD83C\uDDF7\uD83C\uDDFA"));
//        }

        avp256Bot.executeBot(sendMessage);

        stageActivity = false;
    }

    @Override
    public StageInfo getStageInfo() {
        return stageInfo;
    }

    @Override
    public boolean isStageActive() {
        return stageActivity = stageInfo.getKeyword().equals(avp256Bot.getUpdate().getMessage().getText());
    }

    @Override
    public Stage setMessage(TelegramUpdate message) {
        this.telegramUpdate = message;
        return this;
    }

    public InlineKeyboardMarkup getInlineRuEnKeyboard(String callBackData, String textButton) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText(textButton)
                .setCallbackData(stageInfo.getStageName() + callBackData));

        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1));
        return inlineKeyboardMarkup;
    }

    @Override
    public boolean processCallBackQuery() {
        if (avp256Bot.getUpdate().hasCallbackQuery()) {
            String callBackData = avp256Bot.getUpdate().getCallbackQuery().getData();
            if (callBackData.equals(stageInfo.getStageName() + "en_ru")) {
                avp256Bot.updateMessage(stageInfo.getWelcomeMessageRu(),
                        getInlineRuEnKeyboard("ru_en", "EN \uD83C\uDDFA\uD83C\uDDF8"));
            }
            if (callBackData.equals(stageInfo.getStageName() + "ru_en")) {
                avp256Bot.updateMessage(stageInfo.getWelcomeMessageEn(),
                        getInlineRuEnKeyboard("en_ru", "RU \uD83C\uDDF7\uD83C\uDDFA"));
            }
            return true;
        }
        return false;
    }

}

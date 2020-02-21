package com.razykrashka.bot.stage;

import com.razykrashka.bot.service.RazykrashkaBot;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public abstract class MainStage implements Stage {

    @Autowired
    RazykrashkaBot razykrashkaBot;

    protected StageInfo stageInfo;
    protected boolean stageActivity;
    protected Update update;

    @Override
    public void handleRequest() {
        if (this.processCallBackQuery()) return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(update.getMessage().getChat().getId());
        sendMessage.setText(stageInfo.getWelcomeMessageEn());
        sendMessage.setReplyMarkup(getKeyboard());
//        if (stageInfo.getWelcomeMessageRu() != null) {
//            sendMessage.setReplyMarkup(getInlineRuEnKeyboard("en_ru", "RU \uD83C\uDDF7\uD83C\uDDFA"));
//        }

        razykrashkaBot.executeBot(sendMessage);

        stageActivity = false;
    }

    @Override
    public StageInfo getStageInfo() {
        return stageInfo;
    }

    @Override
    public boolean isStageActive() {
        return stageActivity = stageInfo.getKeyword().equals(razykrashkaBot.getUpdate().getMessage().getText());
    }

    @Override
    public Stage setMessage(Update message) {
        this.update = message;
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
        if (razykrashkaBot.getUpdate().hasCallbackQuery()) {
            String callBackData = razykrashkaBot.getUpdate().getCallbackQuery().getData();
            if (callBackData.equals(stageInfo.getStageName() + "en_ru")) {
                razykrashkaBot.updateMessage(stageInfo.getWelcomeMessageRu(),
                        getInlineRuEnKeyboard("ru_en", "EN \uD83C\uDDFA\uD83C\uDDF8"));
            }
            if (callBackData.equals(stageInfo.getStageName() + "ru_en")) {
                razykrashkaBot.updateMessage(stageInfo.getWelcomeMessageEn(),
                        getInlineRuEnKeyboard("en_ru", "RU \uD83C\uDDF7\uD83C\uDDFA"));
            }
            return true;
        }
        return false;
    }
}
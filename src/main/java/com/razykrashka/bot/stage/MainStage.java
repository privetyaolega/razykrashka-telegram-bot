package com.razykrashka.bot.stage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razykrashka.bot.service.RazykrashkaBot;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public abstract class MainStage implements Stage {

    private static List<Map<String, Object>> data;

    @Autowired
    RazykrashkaBot razykrashkaBot;

    protected boolean stageActivity;
    protected StageInfo stageInfo;
    protected Update update;

    static {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        try {
            data = mapper.readValue(new File("src/main/resources/stage/stageStringStorage.json"),
                    new TypeReference<List<Map<String, Map<String, String>>>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleRequest() {
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
        String callBackData = razykrashkaBot.getCallbackQuery().getData();
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

    protected Map<String, String> getStringMap() {
        String className = this.getClass().getSimpleName();
        return (Map<String, String>) data.stream()
                .filter(x -> x.containsKey(className))
                .findFirst().get().get(className);
    }
}
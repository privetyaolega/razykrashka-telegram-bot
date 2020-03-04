package com.razykrashka.bot.stage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razykrashka.bot.db.repo.TelegramMessageRepository;
import com.razykrashka.bot.repository.LocationRepository;
import com.razykrashka.bot.repository.MeetingInfoRepository;
import com.razykrashka.bot.repository.MeetingRepository;
import com.razykrashka.bot.repository.TelegramUserRepository;
import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.ui.helpers.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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
    protected MeetingRepository meetingRepository;
    @Autowired
    protected TelegramUserRepository telegramUserRepository;
    @Autowired
    protected MeetingInfoRepository meetingInfoRepository;
    @Autowired
    protected LocationRepository locationRepository;
    @Autowired
    protected TelegramMessageRepository telegramMessageRepository;

    @Autowired
    protected RazykrashkaBot razykrashkaBot;

    @Autowired
    protected MessageManager messageManager;
    @Autowired
    protected KeyboardBuilder keyboardBuilder;

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

    {
        stageInfo = StageInfo.DEFAULT;
    }

    @Override
    public void handleRequest() {
        messageManager.sendSimpleTextMessage(stageInfo.getWelcomeMessageEn(), getKeyboard());
    }

    @Override
    public StageInfo getStageInfo() {
        return stageInfo;
    }

    @Override
    public boolean isStageActive() {
        if (razykrashkaBot.getRealUpdate().getMessage() != null) {
            return razykrashkaBot.getRealUpdate().getMessage().getText()
                    .equals(this.getStageInfo().getKeyword());
        }
        return false;
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
    public ReplyKeyboard getKeyboard() {
        throw new RuntimeException("IMPLEMENT METHOD IN SPECIFIC CLASS.");
    }

    @Override
    public List<String> getValidKeywords() {
        return Arrays.asList(this.getStageInfo().getKeyword());
    }

    @Override
    public boolean processCallBackQuery() {
        String callBackData = razykrashkaBot.getCallbackQuery().getData();
        if (callBackData.equals(stageInfo.getStageName() + "en_ru")) {
            messageManager.updateMessage(stageInfo.getWelcomeMessageRu(),
                    getInlineRuEnKeyboard("ru_en", "EN \uD83C\uDDFA\uD83C\uDDF8"));
        }
        if (callBackData.equals(stageInfo.getStageName() + "ru_en")) {
            messageManager.updateMessage(stageInfo.getWelcomeMessageEn(),
                    getInlineRuEnKeyboard("en_ru", "RU \uD83C\uDDF7\uD83C\uDDFA"));
        }
        return true;
    }

    protected String getCallBackString(String callBackData) {
        return this.getClass().getSimpleName() + callBackData;
    }

    @Override
    public void setActive(boolean isActive) {
        this.stageActivity = isActive;
    }

    protected Map<String, String> getStringMap() {
        String className = this.getClass().getSimpleName();
        return (Map<String, String>) data.stream()
                .filter(x -> x.containsKey(className))
                .findFirst().get().get(className);
    }

    public boolean getStageActivity() {
        return stageActivity;
    }

    protected void setActiveNextStage(Class clazz) {
        razykrashkaBot.getStages().forEach(stage -> stage.setActive(false));
        Stage stage = ((Stage) razykrashkaBot.getContext().getBean(clazz));
        stage.setActive(true);
    }
}
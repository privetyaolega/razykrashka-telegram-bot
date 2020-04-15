package com.razykrashka.bot.stage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razykrashka.bot.db.repo.*;
import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
@PropertySource(value = "classpath:/props/razykrashka.yaml", factory = YamlPropertyLoaderFactory.class)
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
    @Autowired
    protected UpdateHelper updateHelper;

    protected boolean stageActivity;
    protected StageInfo stageInfo;
    protected Update update;

    static {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        try {
            data = mapper.readValue(new ClassPathResource("bot/stage/stageStringStorage.json").getFile(),
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
        throw new RuntimeException("IMPLEMENT METHOD IN SPECIFIC CLASS.");
    }

    @Override
    public boolean processCallBackQuery() {
        throw new RuntimeException("IMPLEMENT METHOD IN SPECIFIC CLASS.");
    }

    @Override
    public StageInfo getStageInfo() {
        return stageInfo;
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageTextEquals(this.getStageInfo().getKeyword());
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        throw new RuntimeException("IMPLEMENT METHOD IN SPECIFIC CLASS.");
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

    protected String getString(String key) {
        return getStringMap().get(key);
    }

    protected String getFormatString(String key, Object... arg) {
        return String.format(getString(key), arg);
    }

    public ReplyKeyboard getMainKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(StageInfo.SELECT_WAY_MEETING_CREATION.getKeyword()));
        keyboardFirstRow.add(new KeyboardButton(StageInfo.MY_MEETING_VIEW.getKeyword()));
        keyboardFirstRow.add(new KeyboardButton(StageInfo.SELECT_MEETINGS_TYPE.getKeyword()));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(StageInfo.INFORMATION.getKeyword()));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }
}
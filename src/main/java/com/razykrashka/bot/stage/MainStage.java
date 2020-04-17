package com.razykrashka.bot.stage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razykrashka.bot.db.repo.MeetingInfoRepository;
import com.razykrashka.bot.db.repo.MeetingRepository;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
import com.razykrashka.bot.service.RazykrashkaBot;
import com.razykrashka.bot.service.config.YamlPropertyLoaderFactory;
import com.razykrashka.bot.stage.information.InformationStage;
import com.razykrashka.bot.stage.meeting.creation.IntroStartMeetingCreationStage;
import com.razykrashka.bot.stage.meeting.view.all.MyMeetingsViewStage;
import com.razykrashka.bot.stage.meeting.view.all.SelectMeetingsTypeStage;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
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
    protected RazykrashkaBot razykrashkaBot;
    @Autowired
    protected MessageManager messageManager;
    @Autowired
    protected KeyboardBuilder keyboardBuilder;
    @Autowired
    protected UpdateHelper updateHelper;

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

    @Override
    public void handleRequest() {
        throw new RuntimeException("Please, implement method in specific class.");
    }

    @Override
    public void processCallBackQuery() {
        throw new RuntimeException("Please, implement method in specific class.");
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        throw new RuntimeException("Please, implement method in specific class.");
    }

    @Override
    public boolean isStageActive() {
        throw new RuntimeException("Please, implement method in specific class.");
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
        keyboardFirstRow.add(new KeyboardButton(IntroStartMeetingCreationStage.KEYWORD));
        keyboardFirstRow.add(new KeyboardButton(MyMeetingsViewStage.KEYWORD));
        keyboardFirstRow.add(new KeyboardButton(SelectMeetingsTypeStage.KEYWORD));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(InformationStage.KEYWORD));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }
}
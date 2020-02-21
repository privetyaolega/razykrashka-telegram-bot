package com.razykrashka.bot.stage;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

@Log4j2
@Component
public class InformationStage extends MainStage {

    public InformationStage() {
        stageInfo = StageInfo.INFORMATION;
    }


    @Override
    public ReplyKeyboard getKeyboard() {
        return getInlineRuEnKeyboard("en_ru", "RU \uD83C\uDDF7\uD83C\uDDFA");
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }


}

package com.razykrashka.bot.stage.information;

import com.razykrashka.bot.stage.MainStage;
import com.razykrashka.bot.stage.StageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

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
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains() || updateHelper.isMessageTextEquals(this.getStageInfo().getKeyword());
    }
}
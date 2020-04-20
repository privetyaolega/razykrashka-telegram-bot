package com.razykrashka.bot.stage.information;

import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class WelcomeStage extends MainStage {

    public static final String KEYWORD = "/start";

    @Override
    public void handleRequest() {
        messageManager.sendSimpleTextMessage(getString("intro"), getMainKeyboard());
    }

    @Override
    public boolean isStageActive() {
        updateHelper.getUser();
        return updateHelper.isMessageContains("/start")
                && !updateHelper.isMessageFromGroup();
    }
}
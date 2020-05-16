package com.razykrashka.bot.stage.information;

import com.razykrashka.bot.stage.MainStage;
import org.springframework.stereotype.Component;

@Component
public class WelcomeStage extends MainStage {

    public static final String KEYWORD = "/start";

    @Override
    public void handleRequest() {
        updateHelper.getUser();
        messageManager.sendSimpleTextMessage(getString("intro"), getMainKeyboard());
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageContains(KEYWORD)
                && !updateHelper.isMessageFromGroup();
    }
}
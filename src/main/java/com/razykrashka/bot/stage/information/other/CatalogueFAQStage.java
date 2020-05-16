package com.razykrashka.bot.stage.information.other;

import com.razykrashka.bot.constants.Telegraph;
import com.razykrashka.bot.stage.MainStage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class CatalogueFAQStage extends MainStage {

    public final static String KEYWORD = "/catalogue";

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .sendMessage(new SendMessage()
                        .setChatId(updateHelper.getChatId())
                        .setText(Telegraph.TOPICS_CATALOGUE));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageTextEquals(KEYWORD)
                && !updateHelper.isMessageFromGroup();
    }
}
package com.razykrashka.bot.stage.information.main;

import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Log4j2
@Component
public class CatalogueFAQStage extends MainStage {

    public final static String KEYWORD = "/catalogue";

    @Override
    public void handleRequest() {
        messageManager
                .disableKeyboardLastBotMessage()
                .sendMessage(new SendMessage()
                        .setChatId(updateHelper.getChatId())
                        .setText("https://telegra.ph/Meeting-catalogue-04-19"));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageTextEquals(KEYWORD)
                && !updateHelper.isMessageFromGroup();
    }
}
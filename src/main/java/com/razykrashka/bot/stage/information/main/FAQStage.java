package com.razykrashka.bot.stage.information.main;

import com.razykrashka.bot.stage.MainStage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Log4j2
@Component
public class FAQStage extends MainStage {

    public final static String KEYWORD = "/faq";

    @Override
    public void handleRequest() {
        messageManager.sendMessage(new SendMessage()
                .setChatId(updateHelper.getChatId())
                .setText("https://telegra.ph/Razykrashka-English-FAQ-04-28"));
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isMessageTextEquals(KEYWORD)
                && !updateHelper.isMessageFromGroup();
    }
}
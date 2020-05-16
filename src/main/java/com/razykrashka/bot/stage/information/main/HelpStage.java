package com.razykrashka.bot.stage.information.main;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
public class HelpStage extends InformationMainStage {

    public final static String KEYWORD = "/help";

    public HelpStage() {
        buttonLabel = "Help";
    }

    @Override
    public void handleRequest() {
        ReplyKeyboard keyboard = getKeyboardWithHighlightedButton();
        messageManager.updateOrSendDependsOnLastMessageOwner(getString("main"), keyboard);
    }

    @Override
    public void processCallBackQuery() {
        handleRequest();
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}
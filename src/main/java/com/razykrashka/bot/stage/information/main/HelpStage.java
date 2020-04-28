package com.razykrashka.bot.stage.information.main;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class HelpStage extends InformationMainStage {

    public final static String KEYWORD = "/help";

    public HelpStage() {
        buttonLabel = "Help";
    }

    @Override
    public void handleRequest() {
        messageManager.updateOrSendDependsOnLastMessageOwner(getString("main"), getKeyboardWithHighlightedButton());
    }

    @Override
    public void processCallBackQuery() {
        messageManager.updateOrSendDependsOnLastMessageOwner(getString("main"), getKeyboardWithHighlightedButton());
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || updateHelper.isMessageTextEquals(KEYWORD))
                && !updateHelper.isMessageFromGroup();
    }
}
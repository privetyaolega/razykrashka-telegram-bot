package com.razykrashka.bot.stage.information.main;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class InformationStage extends InformationMainStage {

    public static final List<String> KEYWORDS = Arrays.asList("Information", "/info");
    public static final String KEYWORD = "Information";

    public InformationStage() {
        buttonLabel = "Main";
    }

    @Override
    public void processCallBackQuery() {
        messageManager.updateOrSendDependsOnLastMessageOwner(getString("main"), getKeyboardWithHighlightedButton());
    }

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean isStageActive() {
        return (updateHelper.isCallBackDataContains()
                || KEYWORDS.contains(updateHelper.getMessageText()))
                && !updateHelper.isMessageFromGroup();
    }
}
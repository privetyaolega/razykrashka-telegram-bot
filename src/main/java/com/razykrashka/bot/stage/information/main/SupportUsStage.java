package com.razykrashka.bot.stage.information.main;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SupportUsStage extends InformationMainStage {

    public SupportUsStage() {
        buttonLabel = "Support Us";
    }

    @Override
    public void processCallBackQuery() {
        messageManager.updateOrSendDependsOnLastMessageOwner(getString("main"), getKeyboardWithHighlightedButton());
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains();
    }
}